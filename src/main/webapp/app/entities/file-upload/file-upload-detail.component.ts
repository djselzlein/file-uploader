import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IFileUpload } from 'app/shared/model/file-upload.model';

@Component({
    selector: 'jhi-file-upload-detail',
    templateUrl: './file-upload-detail.component.html'
})
export class FileUploadDetailComponent implements OnInit {
    fileUpload: IFileUpload;

    constructor(private activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ fileUpload }) => {
            this.fileUpload = fileUpload;
        });
    }

    previousState() {
        window.history.back();
    }
}
