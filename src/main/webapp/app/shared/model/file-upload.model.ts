import { Moment } from 'moment';

export interface IFileUpload {
    id?: number;
    title?: string;
    description?: string;
    creationDate?: Moment;
    path?: string;
}

export class FileUpload implements IFileUpload {
    constructor(
        public id?: number,
        public title?: string,
        public description?: string,
        public creationDate?: Moment,
        public path?: string
    ) {}
}
