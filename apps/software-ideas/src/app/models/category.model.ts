import { Idea } from './idea.model';
import { Model } from './model.model';
import { Implementation } from './implementation.model';

export interface Category extends Model {
    name: string,
    ideas: Idea[],
    implementations: Implementation[]
}