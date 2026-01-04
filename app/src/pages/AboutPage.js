import React, { useEffect, useState } from 'react';
import parse from 'html-react-parser';
import { Spinner } from 'reactstrap';

export const AboutPage = () => {

    const [loading, setLoading] = useState(false);
    const [html, setHtml] = useState(undefined);

    useEffect(() => {
        setLoading(true);
        fetch('api/about/html')
            .then(response => response.text())
            .then(body => {
                // console.log("response", body)
                setHtml(body);
                setLoading(false);
            });

    }, [setHtml, setLoading])

    if (loading || html == undefined) {
        return (
            <div class="container mt-5">
                <Spinner />
            </div>
        );
    }

    const reactElement = parse(html);
    return (
        <div class="container mt-5 col-5">
            {reactElement}
        </div>
    );
}