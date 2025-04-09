import { Idea } from './idea.model';
import { Model } from './model.model';
import { Innovator } from './innovator.model';

export interface Recommendation extends Model {
    idea: Idea,
    message: string,
    dateTimeCreated: Date,
    innovator: Innovator
}