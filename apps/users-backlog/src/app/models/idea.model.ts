import { Innovator } from './innovator.model';
import { Implementation } from './implementation.model';
import { Category } from './category.model';
import { Model } from './model.model';
import { Recommendation } from './recommendation.model';

export interface Idea extends Model {
    innovator: Innovator,
    summary: string,
    description: string,
    implementations: Implementation[],
    categories: Category[],
    recommendations: Recommendation<Idea>[],
    votes: number
}