import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GlobalPropertiesEditComponent } from './global-properties-edit.component';

describe('GlobalPropertiesEditComponent', () => {
  let component: GlobalPropertiesEditComponent;
  let fixture: ComponentFixture<GlobalPropertiesEditComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GlobalPropertiesEditComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GlobalPropertiesEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
