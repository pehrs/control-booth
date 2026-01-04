import { navigation } from "./navigation";


export function renderMenu(callback) {
    return navigation
        .filter(nav => nav.inMenu)
        .map(nav => {
            return (<li class="nav-item">
                <a id={nav.name + "-Menu"} onClick={callback} page={nav.name}
                    class="btn nav-link top-menu-item" aria-current="page">{nav.name}</a>
            </li>)
        });

} 