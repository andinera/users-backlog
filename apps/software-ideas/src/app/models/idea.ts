import { Innovator } from './innovator';
import { Implementation } from './implementation';

export interface Idea {
    innovator: Innovator,
    summary: string,
    description: string,
    implementations: Implementation[]
}