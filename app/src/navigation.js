import { CatalogPage } from "./pages/Catalog";
import { ApiPage } from "./pages/APIPage";
import { HomePage } from "./pages/HomePage";
import { UserProfilePage } from "./pages/UserProfile";
import { AboutPage } from "./pages/AboutPage";
import { DocsPage } from "./pages/DocsPage";

export const navigation = [
    {
        path: "/about",
        name: "About",
        element: <AboutPage/>,
        inMenu: false,
        isPrivate: false,
    },
    {
        path: "/",
        name: "Home",
        element: <HomePage/>,
        inMenu: false,
        isPrivate: false,
    },  
    {
        path: "/profile",
        name: "UserProfile",
        element: <UserProfilePage/>,
        inMenu: false,
        isPrivate: true,
    },    
    {
        path: "/catalog",
        name: "Catalog",
        element: <CatalogPage/>,
        inMenu: true,
        isPrivate: true,
    },
    {
        path: "/api",
        name: "API",
        element: <ApiPage/>,
        inMenu: true,
        isPrivate: true,
    },    
    {
        path: "/docs",
        name: "Docs",
        element: <DocsPage/>,
        inMenu: true,
        isPrivate: true,
    },
];

