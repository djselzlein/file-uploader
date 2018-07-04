import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { FileUploaderSharedModule } from 'app/shared';
import {
    FileUploadComponent,
    FileUploadDetailComponent,
    FileUploadUpdateComponent,
    FileUploadDeletePopupComponent,
    FileUploadDeleteDialogComponent,
    fileUploadRoute,
    fileUploadPopupRoute
} from './';

const ENTITY_STATES = [...fileUploadRoute, ...fileUploadPopupRoute];

@NgModule({
    imports: [FileUploaderSharedModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [
        FileUploadComponent,
        FileUploadDetailComponent,
        FileUploadUpdateComponent,
        FileUploadDeleteDialogComponent,
        FileUploadDeletePopupComponent
    ],
    entryComponents: [FileUploadComponent, FileUploadUpdateComponent, FileUploadDeleteDialogComponent, FileUploadDeletePopupComponent],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class FileUploaderFileUploadModule {}
