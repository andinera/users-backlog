import { Idea } from './idea';
import { Implementation } from './implementation';

export interface Innovator {
    emailAddress: string,
    ideas: Idea[],
    implementations: Implementation[]
}