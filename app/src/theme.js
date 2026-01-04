

export function setTheme(theme) {
    const htmlElement = document.querySelector('html');
    htmlElement.setAttribute('data-bs-theme', theme);

    localStorage.setItem('theme', theme);
};

export function loadTheme() {
    var theme = localStorage.getItem('theme');
    if (theme === null) {
        theme = "dark";
    }
    const htmlElement = document.querySelector('html');
    htmlElement.setAttribute('data-bs-theme', theme);
};