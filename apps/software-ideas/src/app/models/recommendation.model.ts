import { Idea } from './idea.model';
import { Model } from './model.model';
import { Innovator } from './innovator.model';
import { Implementation } from './implementation.model';
import { Reply } from './reply.model';

export interface Recommendation extends Model {
    idea: Idea,
    implementation: Implementation,
    message: string,
    dateTimeCreated: Date,
    dateTimeModified: Date,
    innovator: Innovator,
    votes: number,
    replies: Reply[]
}