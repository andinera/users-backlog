import { Model } from './model.model';

export interface Product extends Model {
    url: string,
    description: string
}