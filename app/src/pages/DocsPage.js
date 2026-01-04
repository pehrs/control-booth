import AppContext from "../AppContext";

export const DocsPage = () => {

    if (AppContext.profile === undefined) {
        return (<div>n/a</div>)
    }

    return (
        <>
            <h1>Docs</h1>

            <p>WIP</p>
        </>
    );
}