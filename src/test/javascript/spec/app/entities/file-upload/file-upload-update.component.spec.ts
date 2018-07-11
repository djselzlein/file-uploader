/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';

import { FileUploaderTestModule } from '../../../test.module';
import { FileUploadUpdateComponent } from 'app/entities/file-upload/file-upload-update.component';
import { FileUploadService } from 'app/entities/file-upload/file-upload.service';
import { FileUpload } from 'app/shared/model/file-upload.model';

describe('Component Tests', () => {
    describe('FileUpload Management Update Component', () => {
        let comp: FileUploadUpdateComponent;
        let fixture: ComponentFixture<FileUploadUpdateComponent>;
        let service: FileUploadService;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [FileUploaderTestModule],
                declarations: [FileUploadUpdateComponent]
            })
                .overrideTemplate(FileUploadUpdateComponent, '')
                .compileComponents();

            fixture = TestBed.createComponent(FileUploadUpdateComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(FileUploadService);
        });

        describe('save', () => {
            it(
                'Should call create service on save for new entity',
                fakeAsync(() => {
                    // GIVEN
                    const entity = new FileUpload();
                    spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
                    comp.fileUpload = entity;
                    // WHEN
                    comp.save();
                    tick(); // simulate async

                    // THEN
                    expect(service.create).toHaveBeenCalledWith(entity);
                    expect(comp.isSaving).toEqual(false);
                })
            );
        });
    });
});
