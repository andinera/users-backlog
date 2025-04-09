import { Innovator } from './innovator';
import { Idea } from './idea';
import { Product } from './product';

export interface Implementation {
    innovator: Innovator,
    idea: Idea,
    name: string,
    description: string,
    products: Product[]
}