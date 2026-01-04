import { useEffect, useState } from "react";
import { Route, Routes } from "react-router-dom";
import AppLayout from "./AppLayout";
import { navigation } from "./navigation";

export const RenderRoutes = () => {
    // const [authenticated, setAuthenticated] = useState(false);
    // const [user, setUser] = useState(undefined);
    // // const [loading, setLoading] = useState(false);

    // useEffect(() => {
    //     // setLoading(true);
    //     fetch('api/user', { credentials: 'include' }) // <.>
    //         .then(response => response.text())
    //         .then(body => {
    //             console.log("api/user response", body)
    //             if (body === '') {
    //                 setAuthenticated(false);
    //             } else {
    //                 setUser(JSON.parse(body));
    //                 setAuthenticated(true);
    //             }
    //             // setLoading(false);
    //         });
    // }, [setAuthenticated, setUser])

    return (
        <Routes>
            <Route element={<AppLayout />} >
                {navigation.map((route, index) => {
                    // if (route.isPrivate) {
                        return (
                            <Route key={index} path={route.path} element={route.element}>
                                {route.children &&
                                    route.children.map((child, i) => {
                                        <Route key={i} path={child.path} element={child.element} />
                                    })
                                }
                            </Route>
                        )
                    // } else if (!route.isPrivate) {
                    //     return (
                    //         <Route key={index} path={route.path} element={route.element} />
                    //     );
                    // } else {
                    //     return false;
                    // }
                })}
            </Route>
        </Routes>
    );

}