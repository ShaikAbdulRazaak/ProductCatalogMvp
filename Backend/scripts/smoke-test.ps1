param(
    [string]$BaseUrl = "http://localhost:3000"
)

$ErrorActionPreference = "Stop"

function Assert-Status {
    param(
        [string]$Name,
        [scriptblock]$Request,
        [int]$ExpectedStatus
    )

    try {
        $response = & $Request
        if ([int]$response.StatusCode -ne $ExpectedStatus) {
            throw "$Name returned HTTP $($response.StatusCode), expected $ExpectedStatus"
        }
        Write-Output "$($Name): HTTP $($response.StatusCode)"
    } catch {
        if ($_.Exception.Response -and [int]$_.Exception.Response.StatusCode -eq $ExpectedStatus) {
            Write-Output "$($Name): HTTP $ExpectedStatus"
            return
        }
        throw
    }
}

Assert-Status "health" {
    Invoke-WebRequest -UseBasicParsing "$BaseUrl/health"
} 200

Assert-Status "featured products" {
    Invoke-WebRequest -UseBasicParsing "$BaseUrl/api/products/featured"
} 200

Assert-Status "search" {
    Invoke-WebRequest -UseBasicParsing "$BaseUrl/api/products/search?q=shirt"
} 200

Assert-Status "category products" {
    Invoke-WebRequest -UseBasicParsing "$BaseUrl/api/products/category/jewelery"
} 200

Assert-Status "product details" {
    Invoke-WebRequest -UseBasicParsing "$BaseUrl/api/products/1"
} 200

Assert-Status "missing product upstream failure" {
    Invoke-WebRequest -UseBasicParsing "$BaseUrl/api/products/999999"
} 500

$validBody = @{
    productId = 1
    name = "Smoke Test"
    email = "smoke@example.com"
    message = "Smoke test enquiry"
}
$validJsonBody = $validBody | ConvertTo-Json

Assert-Status "valid enquiry" {
    Invoke-WebRequest -UseBasicParsing `
        -Method POST `
        -Uri "$BaseUrl/api/enquiries" `
        -ContentType "application/json" `
        -Body $validJsonBody
} 200

$invalidCases = @(
    @{ Name = "missing product ID"; Body = ($validBody.Clone()); Change = { param($body) $body.productId = 0 } },
    @{ Name = "missing name"; Body = ($validBody.Clone()); Change = { param($body) $body.name = "" } },
    @{ Name = "invalid email"; Body = ($validBody.Clone()); Change = { param($body) $body.email = "invalid" } },
    @{ Name = "missing message"; Body = ($validBody.Clone()); Change = { param($body) $body.message = "" } }
)

foreach ($testCase in $invalidCases) {
    $body = $testCase.Body
    & $testCase.Change $body
    $jsonBody = $body | ConvertTo-Json
    Assert-Status $testCase.Name {
        Invoke-WebRequest -UseBasicParsing `
            -Method POST `
            -Uri "$BaseUrl/api/enquiries" `
            -ContentType "application/json" `
            -Body $jsonBody
    } 400
}

Write-Output "Backend smoke tests passed."
