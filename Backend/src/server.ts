// @ts-ignore Express types are provided by the project's dependency configuration.
import express, { NextFunction, Request, Response } from 'express';

const app = express();
const environment = (globalThis as {
    process?: { env?: Record<string, string | undefined> };
}).process?.env ?? {};
const configuredPort = Number(environment.PORT ?? 3000);
const PORT = Number.isInteger(configuredPort) && configuredPort > 0 && configuredPort <= 65535
    ? configuredPort
    : 3000;
const FAKE_STORE_API = (environment.FAKE_STORE_API ?? 'https://fakestoreapi.com').replace(/\/+$/, '');

interface FakeStoreProduct {
    title: string;
    description: string;
}

async function fetchFromFakeStore<T>(path: string): Promise<T> {
    const response = await fetch(`${FAKE_STORE_API}${path}`);

    if (!response.ok) {
        throw new Error(`Fake Store API returned HTTP ${response.status}`);
    }

    return response.json() as Promise<T>;
}

app.use((req: Request, res: Response, next: NextFunction): void => {
    res.header('Access-Control-Allow-Origin', '*');
    res.header('Access-Control-Allow-Methods', 'GET,POST,PUT,PATCH,DELETE,OPTIONS');
    res.header('Access-Control-Allow-Headers', 'Content-Type, Authorization');

    if (req.method === 'OPTIONS') {
        res.sendStatus(204);
        return;
    }

    next();
});
app.use(express.json());

app.get('/health', (req: Request, res: Response) => {
    res.status(200).json({ status: 'ok' });
});

// Home page: 5 featured products
app.get('/api/products/featured', async (req: Request, res: Response) => {
    try {
        const data = await fetchFromFakeStore('/products?limit=5');
        res.json(data);
    } catch (error) {
        console.error('Featured products request failed:', error);
        res.status(500).json({ error: 'Failed to fetch featured products' });
    }
});

// Category page
app.get('/api/products/category/:category', async (req: Request, res: Response) => {
    try {
        const category = encodeURIComponent(req.params.category);
        const data = await fetchFromFakeStore(`/products/category/${category}`);
        res.json(data);
    } catch (error) {
        console.error('Category products request failed:', error);
        res.status(500).json({ error: 'Failed to fetch category products' });
    }
});

// Search functionality (filtering Fake Store data on the backend)
app.get('/api/products/search', async (req: Request, res: Response) => {
    const query = (req.query.q as string)?.toLowerCase() || '';
    try {
        const allProducts = await fetchFromFakeStore<FakeStoreProduct[]>('/products');
        const filtered = allProducts.filter((product) =>
            product.title.toLowerCase().includes(query) || product.description.toLowerCase().includes(query)
        );
        res.json(filtered);
    } catch (error) {
        console.error('Product search request failed:', error);
        res.status(500).json({ error: 'Failed to search products' });
    }
});

// Get a specific product by ID
app.get('/api/products/:id', async (req: Request, res: Response) => {
    try {
        const productId = encodeURIComponent(req.params.id);
        const data = await fetchFromFakeStore(`/products/${productId}`);
        res.json(data);
    } catch (error) {
        console.error('Product details request failed:', error);
        res.status(500).json({ error: 'Failed to fetch product details' });
    }
});

// Enquiry submission (Email provider stub)
app.post('/api/enquiries', (req: Request, res: Response) => {
    const { productId, name, email, message } = req.body ?? {};
    const normalizedName = typeof name === 'string' ? name.trim() : '';
    const normalizedEmail = typeof email === 'string' ? email.trim() : '';
    const normalizedMessage = typeof message === 'string' ? message.trim() : '';
    const normalizedProductId = Number(productId);

    if (!Number.isInteger(normalizedProductId) || normalizedProductId <= 0) {
        res.status(400).json({ error: 'A valid productId is required' });
        return;
    }

    if (!normalizedName) {
        res.status(400).json({ error: 'Name is required' });
        return;
    }

    if (!/^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$/.test(normalizedEmail)) {
        res.status(400).json({ error: 'A valid email is required' });
        return;
    }

    if (!normalizedMessage) {
        res.status(400).json({ error: 'Message is required' });
        return;
    }

    console.log(`[EMAIL STUB] Enquiry received for Product ${normalizedProductId} from ${normalizedName} (${normalizedEmail})`);
    res.status(200).json({ message: 'Thank you for your enquiry. Our sales team will contact you shortly.' });
});

app.listen(PORT, () => {
    console.log(`BFF Server running locally on http://localhost:${PORT}!`);
});