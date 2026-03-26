import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddonOutboundComponent } from './addon-outbound.component';

describe('AddonOutboundComponent', () => {
  let component: AddonOutboundComponent;
  let fixture: ComponentFixture<AddonOutboundComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AddonOutboundComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AddonOutboundComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
