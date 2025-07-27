import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'formatBytes',
  standalone: true, 
})
export class FormatBytesPipe implements PipeTransform {

  transform(bytes: number, decimals: number = 2): string {
    if(bytes < 0) return '0 Bytes';

    const k = 1024;
    const dm = decimals < 0 ? 0 : decimals;
    const sizes = ['Bytes', 'MB', 'GB', 'TB'];

    const i = Math.floor(Math.log(bytes)/Math.log(k));

    return parseFloat((bytes / Math.pow(k,i)).toFixed(dm)) + ' ' + sizes[i - 1];
  }

}
