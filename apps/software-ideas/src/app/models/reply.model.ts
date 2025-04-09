import { Model } from './model.model';
import { Innovator } from './innovator.model';
import { Recommendation } from './recommendation.model';

export interface Reply extends Model {
    recommendation: Recommendation,
    message: string,
    dateTimeCreated: Date,
    dateTimeModified: Date,
    innovator: Innovator
}