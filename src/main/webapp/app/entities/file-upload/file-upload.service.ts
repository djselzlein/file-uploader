import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_FORMAT } from 'app/shared/constants/input.constants';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { IFileUpload } from 'app/shared/model/file-upload.model';

type EntityResponseType = HttpResponse<IFileUpload>;
type EntityArrayResponseType = HttpResponse<IFileUpload[]>;

@Injectable({ providedIn: 'root' })
export class FileUploadService {
    private resourceUrl = SERVER_API_URL + 'api/file-uploads';

    constructor(private http: HttpClient) {}

    create(fileUpload: IFileUpload): Observable<EntityResponseType> {
        const copy = this.convertDateFromClient(fileUpload);
        return this.http
            .post<IFileUpload>(this.resourceUrl, copy, { observe: 'response' })
            .map((res: EntityResponseType) => this.convertDateFromServer(res));
    }

    update(fileUpload: IFileUpload): Observable<EntityResponseType> {
        const copy = this.convertDateFromClient(fileUpload);
        return this.http
            .put<IFileUpload>(this.resourceUrl, copy, { observe: 'response' })
            .map((res: EntityResponseType) => this.convertDateFromServer(res));
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http
            .get<IFileUpload>(`${this.resourceUrl}/${id}`, { observe: 'response' })
            .map((res: EntityResponseType) => this.convertDateFromServer(res));
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http
            .get<IFileUpload[]>(this.resourceUrl, { params: options, observe: 'response' })
            .map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res));
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    postFile(fileUploadId: number, fileToUpload: File): Observable<any> {
        const formData: FormData = new FormData();
        formData.append('file', fileToUpload, fileToUpload.name);
        return this.http.post(this.resourceUrl + '/' + fileUploadId + '/upload', formData, { observe: 'response' });
    }

    private convertDateFromClient(fileUpload: IFileUpload): IFileUpload {
        const copy: IFileUpload = Object.assign({}, fileUpload, {
            creationDate:
                fileUpload.creationDate != null && fileUpload.creationDate.isValid() ? fileUpload.creationDate.format(DATE_FORMAT) : null
        });
        return copy;
    }

    private convertDateFromServer(res: EntityResponseType): EntityResponseType {
        res.body.creationDate = res.body.creationDate != null ? moment(res.body.creationDate) : null;
        return res;
    }

    private convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
        res.body.forEach((fileUpload: IFileUpload) => {
            fileUpload.creationDate = fileUpload.creationDate != null ? moment(fileUpload.creationDate) : null;
        });
        return res;
    }
}
