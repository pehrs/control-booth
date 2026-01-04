import React, { useEffect, useState } from 'react';
import { useCookies } from 'react-cookie';


import './App.css';
import { navigation, } from './navigation';
import { Spinner } from 'reactstrap';

const AppOrg = () => {
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState(getNavigationPage("Home"))
  const [authenticated, setAuthenticated] = useState(false);
  const [user, setUser] = useState(undefined);
  const [cookies] = useCookies(['XSRF-TOKEN']); // <.>

  // console.log("href", window.location.href);
  const url = new URL(window.location.href);
  console.log("url", url);
  console.log("hash", url.hash);

  useEffect(() => {
    setLoading(true);
    fetch('api/user', { credentials: 'include' }) // <.>
      .then(response => response.text())
      .then(body => {
        console.log("api/user response", body)
        if (body === '') {
          setAuthenticated(false);
        } else {
          setUser(JSON.parse(body));
          setAuthenticated(true);
        }
        setLoading(false);
      });
  }, [setAuthenticated, setLoading, setUser])

  console.log("user", user);

  const login = () => {
    let port = (window.location.port ? ':' + window.location.port : '');
    if (port === ':3030') {
      port = ':8080';
    }
    window.location.href = `//${window.location.hostname}${port}/api/private`;
  }

  const logout = () => {
    fetch('/api/logout', {
      method: 'POST', credentials: 'include',
      headers: { 'X-XSRF-TOKEN': cookies['XSRF-TOKEN'] } // <.>
    })
      .then(res => res.json())
      .then(response => {
        window.location.href = `${response.logoutUrl}?id_token_hint=${response.idToken}`
          + `&post_logout_redirect_uri=${window.location.origin}`;
      });
  }

  // console.log("page", page);
  const pageElement = page === undefined ? undefined : page.element;


  function getNavigationPage(name) {
    for (var navIndex in navigation) {
      const nav = navigation[navIndex];
      if (nav.name === name) {
        return nav;
      }
    }
    return undefined;
  }

  function selectPage(ev) {
    const pageName = ev.target.getAttribute("page");
    // console.log("Select page", pageName)
    setPage(getNavigationPage(pageName))

    const topMenuItems = document.getElementsByClassName("top-menu-item");
    // console.log("topMenuItems", topMenuItems)
    if (topMenuItems) {
      for (var i in topMenuItems) {
        const el = topMenuItems[i];
        if (el.nodeName === "A") {
          el.classList.remove("active");
          //console.log("element", topMenuItems[i])
        }
      }
    }
    const el = document.getElementById(pageName + "-Menu")
    if (el) {
      el.classList.add("active")
    }
  }

  const menuItems = navigation
    .filter(nav => nav.inMenu)
    .map(nav => {
      return (<li class="nav-item">
        <a id={nav.name + "-Menu"} page={nav.name} class="btn nav-link top-menu-item" aria-current="page" onClick={selectPage}>{nav.name}</a>
      </li>)
    })

  if (loading) {
    return (<Spinner></Spinner>)
  }

  return (
    <>

      <nav class="me-auto navbar navbar-expand-lg navbar-dark bg-dark">
        <div class="container-fluid">
          <a class="navbar-brand" data-bs-toggle="offcanvas" href="#offcanvasExample" role="button" aria-controls="offcanvasExample">
            <button class="btn btn-secondary"><i class="fa-solid fa-bars"></i></button>
          </a>
          <a class="navbar-brand btn" page="Home" onClick={selectPage}>
            Control-Booth
          </a>
          <button class="navbar-toggler" type="button"
            data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent"
            aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
          </button>
          <div class="collapse navbar-collapse" id="navbarSupportedContent">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
              {authenticated && (
                menuItems
              )}
            </ul>
            {!authenticated && (
              <button className='btn btn-primary' onClick={login}>Login</button>
            )}
            {authenticated && (
              <ul class="navbar-nav mb-2 mb-lg-0 ">
                <li class="nav-item dropdown ">
                  <a class="nav-link  dropdown-toggle" href="#" id="navbarDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                    <i class="fa-solid fa-user"></i> {user.preferred_username}
                  </a>
                  <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="navbarDropdown">
                    <li><a class="btn dropdown-item" page="UserProfile" onClick={selectPage}>Profile</a></li>

                    <li><hr class="dropdown-divider" /></li>
                    <li><a class="dropdown-item" href="/#dark">Dark</a></li>
                    <li><a class="dropdown-item" href="/#light">Light</a></li>

                    <li><hr class="dropdown-divider" /></li>
                    <li><a class="dropdown-item" onClick={logout}>Logout</a></li>
                  </ul>
                </li>
              </ul>
            )}
          </div>
        </div>

      </nav>
      <div class="container-fluid mt-1 col-12">

        {pageElement}

      </div>

      {/* Left Offcanvas menu */}
      <div class="offcanvas offcanvas-start" tabindex="-1" id="offcanvasExample" aria-labelledby="offcanvasExampleLabel">
        <div class="offcanvas-header">
          <h5 class="offcanvas-title" id="offcanvasExampleLabel">Offcanvas</h5>
          <button type="button" class="btn-close" data-bs-dismiss="offcanvas" aria-label="Close"></button>
        </div>
        <div class="offcanvas-body">
          <div>
            Some text as placeholder. In real life you can have the elements you have chosen. Like, text, images, lists, etc.
          </div>
          <div class="dropdown mt-3">
            <button class="btn btn-primary dropdown-toggle" type="button" data-bs-toggle="dropdown">
              Dropdown button
            </button>
            <ul class="dropdown-menu">
              <li><a class="dropdown-item" href="#">Action</a></li>
              <li><a class="dropdown-item" href="#">Another action</a></li>
              <li><a class="dropdown-item" href="#">Something else here</a></li>
            </ul>
          </div>
        </div>
      </div>

    </>
  );
}



export default AppOrg;
