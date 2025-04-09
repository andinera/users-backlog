import { Model } from './model.model';
import { Innovator } from './innovator.model';
import { Recommendation } from './recommendation.model';

export interface Reply<T> extends Model {
    recommendation: Recommendation<T>,
    message: string,
    dateTimeCreated: Date,
    dateTimeModified: Date,
    innovator: Innovator
}