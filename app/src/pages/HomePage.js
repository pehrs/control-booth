import AppContext from "../AppContext";
import { AboutPage } from "./AboutPage";

export const HomePage = () => {

    if (AppContext.profile === undefined) {
        return (<AboutPage />)
    }

    return (
        <div className="container-fluid col-12">
            <h1>Home page</h1>
            <p>WIP</p>
        </div>
    );
}