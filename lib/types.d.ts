export type NavBarHeightChangedEvent = {
    navBarHeight: number;
};
export interface NavBarListener {
    addNavBarHeightListener(callback: (height: number) => void): () => void;
    getNavBarHeight(): Promise<number>;
}
