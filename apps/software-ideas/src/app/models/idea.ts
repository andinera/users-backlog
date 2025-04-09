import { Innovator } from './innovator';
import { Implementation } from './implementation';
import { Category } from './category';

export interface Idea {
    innovator: Innovator,
    summary: string,
    description: string,
    implementations: Implementation[],
    categories: Category[]
}