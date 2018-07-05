import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { IFileUpload } from 'app/shared/model/file-upload.model';
import { FileUploadService } from './file-upload.service';

import * as moment from 'moment';

@Component({
    selector: 'jhi-file-upload-update',
    templateUrl: './file-upload-update.component.html'
})
export class FileUploadUpdateComponent implements OnInit {
    private _fileUpload: IFileUpload;
    isSaving: boolean;
    creationDateDp: any;

    fileToUpload: File = null;

    constructor(private fileUploadService: FileUploadService, private activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ fileUpload }) => {
            this.fileUpload = fileUpload;
        });
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        // if (this.fileUpload.id !== undefined) {
        //     this.subscribeToSaveResponse(this.fileUploadService.update(this.fileUpload));
        // } else {
        //     this.subscribeToSaveResponse(this.fileUploadService.create(this.fileUpload));
        // }
        this.fileUploadService.postFile(this.fileToUpload);

        this.subscribeToSaveResponse(this.fileUploadService.create(this.fileUpload));
    }

    handleFileInput(event) {
        this.fileToUpload = event.target.files.item(0);
        this.fileUpload.title = this.fileToUpload.name;
        this.fileUpload.creationDate = moment(this.fileToUpload.lastModifiedDate);
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<IFileUpload>>) {
        result.subscribe((res: HttpResponse<IFileUpload>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError(null));
    }

    private onSaveSuccess() {
        this.isSaving = false;
        this.previousState();
    }

    private onSaveError(error) {
        this.isSaving = false;
    }
    get fileUpload() {
        return this._fileUpload;
    }

    set fileUpload(fileUpload: IFileUpload) {
        this._fileUpload = fileUpload;
    }
}
