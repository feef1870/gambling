import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Labor } from './labor';

describe('Labor', () => {
  let component: Labor;
  let fixture: ComponentFixture<Labor>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Labor],
    }).compileComponents();

    fixture = TestBed.createComponent(Labor);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
