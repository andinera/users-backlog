import { Idea } from './idea.model';
import { Model } from './model.model';

export interface Category extends Model {
    name: string,
    ideas: Idea[]
}