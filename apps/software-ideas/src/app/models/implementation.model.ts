import { Innovator } from './innovator.model';
import { Idea } from './idea.model';
import { Product } from './product.model';
import { Model } from './model.model';
import { Category } from './category.model';
import { Recommendation } from './recommendation.model';

export interface Implementation extends Model {
    innovator: Innovator,
    ideas: Idea[],
    name: string,
    description: string,
    products: Product[],
    categories: Category[],
    votes: number,
    recommendations: Recommendation[]
}