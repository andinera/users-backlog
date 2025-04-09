import { Innovator } from './innovator.model';
import { Idea } from './idea.model';
import { Product } from './product.model';
import { Model } from './model.model';

export interface Implementation extends Model {
    innovator: Innovator,
    ideas: Idea[],
    name: string,
    description: string,
    products: Product[]
}