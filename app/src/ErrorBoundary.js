import React from "react";

export class ErrorBoundary extends React.Component {
    constructor(props) {
        super(props);
        this.state = { hasError: false };
    }

    static getDerivedStateFromError(error) {
        // Update state so the next render will show the fallback UI.
        return { hasError: true };
    }

    componentDidCatch(error, errorInfo) {
        console.error("----ERROR----", { error, errorInfo });
        // You can also log the error to an error reporting service
        // logErrorToMyService(error, errorInfo);
    }

    render() {
        console.log("state", this.state)
        console.log("err", this.err)
        if (this.state && this.state.hasError === true)  {
            return (
                <>
                    <h1>Something went wrong.</h1>
                    <pre>{"" + JSON.stringify(this.state, 2, "  ")}</pre>
                    <a href="/">Go to home</a>
                </>
            );
        }

        return this.props.children;
    }
}