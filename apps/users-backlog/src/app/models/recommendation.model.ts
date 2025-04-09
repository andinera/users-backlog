import { Idea } from './idea.model';
import { Model } from './model.model';
import { Innovator } from './innovator.model';
import { Reply } from './reply.model';

export interface Recommendation<T> extends Model {
    idea: Idea,
    parent: T,
    message: string,
    dateTimeCreated: Date,
    dateTimeModified: Date,
    innovator: Innovator,
    votes: number,
    replies: Reply<T>[]
}