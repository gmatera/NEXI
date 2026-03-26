import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddonInboundComponent } from './addon-inbound.component';

describe('AddonInboundComponent', () => {
  let component: AddonInboundComponent;
  let fixture: ComponentFixture<AddonInboundComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AddonInboundComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AddonInboundComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
