import { useEffect, useState } from "react";
import AppContext from "../AppContext";
import { redirect, useNavigate } from "react-router-dom";
import { Spinner } from "reactstrap";

export const CatalogPage = () => {
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(undefined);
    const [entities, setEntities] = useState(undefined);
    const navigate = useNavigate();

    console.log("profile", AppContext.profile);

    useEffect(() => {
        setLoading(true);
        console.log("calling api/entity")
        fetch('api/entity', { credentials: 'include' })
            .then(response => response.text())
            .then(body => {
                const ents = JSON.parse(body);
                console.log("ENTITY RESPONSE", ents)
                setEntities(ents);
                setLoading(false);
            })
            .catch(error => {
                setError(error)
            });
    }, [setLoading, setEntities, setError])

    console.log("entities", entities);

    if (error) {
        return (<pre>{"" + error}</pre>)
    }

    if (loading) {
        return <Spinner />
    }

    const rendered = entities !== undefined ? entities.map((entity) => {

        const annotations = entity.annotations === undefined ? [] :
            Object.keys(entity.annotations).map(id => {
                const value = entity.annotations[id]
                return (
                    <div>{id}: <b>{value}</b></div>
                );
            })

        console.log("annotations", JSON.stringify(entity.annotations, "", "  "))
        console.log("annotations.keys", JSON.stringify(entity.annotations.keys, "", "  "))

        return (
            <tr>
                <th scope="row">{entity.id}</th>
                <td>{entity.entityType}/{entity.entityVariant}</td>
                <td><b>{entity.displayName}</b></td>
                <td>{entity.name}</td>
                <td>{annotations}</td>
            </tr>
        )
    }) : []

    return (
        <>
            <h1>Catalog</h1>
            <table class="table table-striped">
                <thead>
                    <tr>
                        <th scope="col">Id</th>
                        <th scope="col">Type/Variant</th>
                        <th scope="col">Display Name</th>
                        <th scope="col">Name</th>
                        <th scope="col">Annotations</th>
                    </tr>
                </thead>
                <tbody>
                    {rendered}
                </tbody>
            </table>
        </>
    );
};