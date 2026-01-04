import { useEffect, useState } from "react";
import { useCookies } from "react-cookie";
import { Spinner } from "reactstrap";
import AppContext from '../AppContext';

export const UserProfilePage = () => {
    
    const profile = AppContext.profile;

    if(profile === undefined) {
        return (<>n/a</>)
    }
    return (
        <>
            <h1>{profile.user.displayName}</h1>
            {profile.user.picture && (
                <img height={128} src={"data:image/png;base64, " + profile.user.picture}/>
            )}
            <h3>{profile.user.email}</h3>

            <h2 class="mt-4">Details</h2>
            <pre>{JSON.stringify(profile, "", 2)}</pre>
        </>
    )
};