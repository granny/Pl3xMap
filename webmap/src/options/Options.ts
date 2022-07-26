export class Options {
    private _format: string = 'png';

    get format(): string {
        return this._format;
    }

    set format(value: string) {
        this._format = value;
    }
}
