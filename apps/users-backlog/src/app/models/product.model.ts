import { Model } from './model.model';
import { Implementation } from './implementation.model';

export interface Product extends Model {
    url: string,
    description: string,
    implementation: Implementation
}