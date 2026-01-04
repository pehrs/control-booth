import React, { useEffect, useState } from 'react';
import { useCookies } from 'react-cookie';
import AppContext from './AppContext';
import './AppLayout.css';
import { navigation, } from './navigation';
import { Spinner } from 'reactstrap';
import { Outlet, useNavigate } from 'react-router-dom';
import { renderMenu } from './RenderMenu';
import { loadTheme, setTheme } from './theme';

const AppLayout = () => {
  const [loading, setLoading] = useState(false);
  const [authenticated, setAuthenticated] = useState(false);
  const [user, setUser] = useState(undefined);
  const [cookies] = useCookies(['XSRF-TOKEN']);

  const navigate = useNavigate();


  // console.log("href", window.location.href);
  const url = new URL(window.location.href);
  // console.log("url", url);
  // console.log("hash", url.hash);

  useEffect(() => {
    setLoading(true);
    fetch('api/user/profile', { credentials: 'include' }) // <.>
      .then(response => response.text())
      .then(body => {
        //console.log("api/user response", body)
        loadTheme();

        if (body === '') {
          setAuthenticated(false);
        } else {
          const profile = JSON.parse(body);
          setUser(profile);
          setAuthenticated(true);
          AppContext.profile = profile;
        }
        setLoading(false);
      });

  }, [setAuthenticated, setLoading, setUser])

  const login = () => {
    let port = (window.location.port ? ':' + window.location.port : '');
    if (port === ':3030') {
      port = ':8080';
    }
    const redirectUrl = "" + window.location;
    const loginUrl = `//${window.location.hostname}${port}/api/user/login?to=${redirectUrl}`;
    console.log("LOGIN URL", loginUrl)
    window.location.href = loginUrl;
  }

  const logout = () => {
    AppContext.profile = undefined;
    fetch('/api/user/logout', {
      method: 'POST', credentials: 'include',
      headers: { 'X-XSRF-TOKEN': cookies['XSRF-TOKEN'] } // <.>
    })
      .then(res => res.json())
      .then(response => {
        window.location.href = `${response.logoutUrl}?id_token_hint=${response.idToken}`
          + `&post_logout_redirect_uri=${window.location.origin}`;
      });
  }


  function getNavigationPath(name) {
    for (var navIndex in navigation) {
      const nav = navigation[navIndex];
      if (nav.name === name) {
        return nav.path;
      }
    }
    return undefined;
  }

  function selectPage(ev) {
    const pageName = ev.target.getAttribute("page");
    console.log("Select page", pageName)
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

    const path = getNavigationPath(pageName)
    if (path) {
      // console.log("navigate", path)
      navigate(path)
    }
  }

  function setThemeCallback(ev) {
    const theme = ev.target.getAttribute("theme");
    setTheme(theme)
  };

  const menuItems = renderMenu(selectPage)

  if (loading) {
    return (<Spinner></Spinner>)
  }

  return (
    <>
  {/* navbar-dark bg-dark */}
      <header class="me-auto navbar navbar-expand-lg sticky-top flex-md-nowrap p-0  mt-2 top-navbar" data-bs-theme="dark">
        <div class="container-fluid">
          {/* <a class="navbar-brand" data-bs-toggle="offcanvas" href="#offcanvasExample" role="button" aria-controls="offcanvasExample">
            <button class="btn btn-secondary"><i class="fa-solid fa-bars"></i></button>
          </a> */}
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
            {AppContext.profile !== undefined && (
              <a class="btn dropdown-menu-end" page="About" onClick={selectPage}>About</a>
            )}
            {authenticated && (
              <ul class="navbar-nav mb-2 mb-lg-0">
                <li class="nav-item dropdown " data-bs-theme="dark">
                  <a class="nav-link  dropdown-toggle" href="#" id="navbarDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                    <i class="fa-solid fa-user"></i> {user.user.displayName}
                  </a>
                  <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="navbarDropdown">
                    <li><a class="btn dropdown-item" page="UserProfile" onClick={selectPage}>Profile</a></li>

                    <li><hr class="dropdown-divider" /></li>
                    <li><a class="dropdown-item" onClick={setThemeCallback} theme="dark">Dark</a></li>
                    <li><a class="dropdown-item" onClick={setThemeCallback} theme="light">Light</a></li>

                    <li><hr class="dropdown-divider" /></li>
                    <li><a class="dropdown-item" onClick={logout}>Logout</a></li>
                  </ul>
                </li>
              </ul>
            )}
          </div>
        </div>
      </header>

      <div class="container-fluid mt-1 col-12" >
        <div class="row">
          <main class="col-md-12">
            <Outlet />
          </main>
        </div>
      </div>

      {/* Left Offcanvas menu */}
      <div class="offcanvas offcanvas-start" tabindex="-1" id="offcanvasExample" aria-labelledby="offcanvasExampleLabel">
        <div class="offcanvas-header">
          <h5 class="offcanvas-title" id="offcanvasExampleLabel">WIP Menu</h5>
          <button type="button" class="btn-close" data-bs-dismiss="offcanvas" aria-label="Close"></button>
        </div>
        <div class="offcanvas-body">

          <ul class="navbar-nav mb-2 mb-lg-0 ">
            <li class="nav-link">
              <a class="" type="button" href="/about">
                About
              </a>
            </li>
          </ul>

        </div>
      </div>

    </>
  );
}



export default AppLayout;
