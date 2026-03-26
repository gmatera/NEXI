import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FtsOutboundComponent } from './fts-outbound.component';

describe('FtsOutboundComponent', () => {
  let component: FtsOutboundComponent;
  let fixture: ComponentFixture<FtsOutboundComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ FtsOutboundComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FtsOutboundComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
