import { Idea } from './idea.model';
import { Implementation } from './implementation.model';
import { Model } from './model.model';

export interface Innovator extends Model {
    emailAddress: string,
    ideas: Idea[],
    implementations: Implementation[]
}