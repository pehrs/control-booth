import AppContext from "../AppContext";

export const ApiPage = () => {
    if (AppContext.profile === undefined) {
        return (<div>n/a</div>)
    }

    return (
        <>
            <h1>API</h1>

            <p>WIP</p>
        </>
    );
}