import { Component, OnInit, OnDestroy } from '@angular/core';
import { tap, takeUntil, catchError, first } from 'rxjs/operators';
import { ReplaySubject, of } from 'rxjs';

import { CategoryService } from 'src/app/services/category.service';
import { IdeaService } from '../../services/idea.service';
import { Idea } from '../../models/idea';
import { Category } from 'src/app/models/category';

@Component({
  selector: 'app-ideas',
  templateUrl: './ideas.component.html',
  styleUrls: ['./ideas.component.css']
})
export class IdeasComponent implements OnInit, OnDestroy {
  
  title = 'project-ideas';
  categoriesOfIdeas = [];
    
  private _destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);

  constructor (
    private readonly _ideaService: IdeaService,
    private readonly _categoryService: CategoryService
  ) {
  }

  ngOnInit() {
    this._categoryService.getAllCategories().pipe(
      first(),
      tap((categories: Category[]) => {
        if (categories) {
          categories.forEach((category: Category) => {
            this._ideaService.getIdeas(category.name).pipe(
              first(),
              tap((ideas: Idea[]) => {
                if (ideas) {
                  this.categoriesOfIdeas.push({category: category,
                                               ideas: ideas});
                }
              }),
              takeUntil(this._destroyed$),
              catchError((e: any) => {
                console.log(e);
                return of(null);
              })
            ).subscribe();
          })
        }
      }),
      takeUntil(this._destroyed$),
      catchError((e: any) => {
        console.log(e);
        return of(null);
      })
    ).subscribe();
  }

  ngOnDestroy(): void {
    this._destroyed$.next(true);
    this._destroyed$.complete();
  }

  onLeftClick(event: any, id: string) {
    const cardBlockContainer = document.getElementById(id) as HTMLElement;
    const cardBlocks = Array.from(document.getElementById(id).getElementsByClassName('card-block'));
    const leftButton = document.getElementById(`left-button-${id}`) as HTMLElement;
    const cardBlockContainerLeftEdge = cardBlockContainer.scrollLeft;
    for (const cardBlock of cardBlocks) {
      const cb = cardBlock as HTMLElement;
      const cbLeftEdge = cb.offsetLeft - leftButton.offsetWidth;
      const numBlocksShift = Math.floor(cardBlockContainer.offsetWidth / cb.offsetWidth);
      if (cbLeftEdge + (numBlocksShift * cb.offsetWidth) >= cardBlockContainerLeftEdge) {
        const scrollToOptions = {
          left: cbLeftEdge,
          behavior: 'smooth'
        } as ScrollToOptions;
        cardBlockContainer.scrollTo(scrollToOptions);
        break;
      }
    }
  }

  onRightClick(event: any, id: string) {
    const cardBlockContainer = document.getElementById(id) as HTMLElement;
    const cardBlocks = Array.from(document.getElementById(id).getElementsByClassName('card-block'));
    const leftButton = document.getElementById(`left-button-${id}`) as HTMLElement;
    const cardBlockContainerRightEdge = cardBlockContainer.scrollLeft + cardBlockContainer.offsetWidth;
    for (const cardBlock of cardBlocks) {
      const cb = cardBlock as HTMLElement;
      const cbLeftEdge = cb.offsetLeft - leftButton.offsetWidth;
      if (cbLeftEdge + cb.offsetWidth > cardBlockContainerRightEdge) {
        const scrollToOptions = {
          left: cbLeftEdge,
          behavior: 'smooth'
        } as ScrollToOptions;
        cardBlockContainer.scrollTo(scrollToOptions);
        break;
      }
    }
  }
}
