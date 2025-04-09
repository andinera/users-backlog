import { Innovator } from './innovator';
import { Idea } from './idea';

export interface Implementation {
    source: string,
    implementer: Innovator,
    idea: Idea,
    name: string
}