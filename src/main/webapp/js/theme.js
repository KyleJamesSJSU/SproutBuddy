/* =====================================================================
   SproutBuddy – theme.js
   Seasonal colour theme for the app screens (dashboard, plant profile,
   add plant).

   - Load it in <head>, AFTER css/seasons.css, so the right colours are in
     place before anything is drawn.
   - Sets  data-season="spring|summer|fall|winter"  on <html>; the colours
     live in css/seasons.css.
   - The choice is 'auto' (follow today's date) or one of the four seasons.
     It is remembered in this browser (localStorage).
     TODO backend: store it with the user's account instead.
   - If the page has a #settings-dialog and a #btn-settings button, this
     file also turns the gear button into the settings popup (season picker).

   The log-in and sign-up pages do NOT load this, on purpose: they keep the
   original SproutBuddy look in every season.
   ===================================================================== */
(function () {
    'use strict';

    var KEY = 'sproutbuddy-season';
    var SEASONS = ['spring', 'summer', 'fall', 'winter'];

    var GEAR =
        '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" ' +
        'stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><circle cx="12" cy="12" r="3"/>' +
        '<path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 1 1-2.83 2.83l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-4 0v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 1 1-2.83-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1 0-4h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 1 1 2.83-2.83l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 4 0v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 1 1 2.83 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 0 4h-.09a1.65 1.65 0 0 0-1.51 1z"/></svg>';

    /* ---------- season logic ---------- */

    // Northern-hemisphere meteorological seasons.
    function autoSeason(date) {
        var m = (date || new Date()).getMonth();   // 0 = January
        if (m >= 2 && m <= 4) return 'spring';     // Mar - May
        if (m >= 5 && m <= 7) return 'summer';     // Jun - Aug
        if (m >= 8 && m <= 10) return 'fall';      // Sep - Nov
        return 'winter';                           // Dec - Feb
    }

    // What the user picked: 'auto' or a season name.
    function get() {
        try {
            var v = localStorage.getItem(KEY);
            if (v === 'auto' || SEASONS.indexOf(v) !== -1) return v;
        } catch (e) { /* storage unavailable */ }
        return 'auto';
    }

    // The season actually shown.
    function current() {
        var v = get();
        return v === 'auto' ? autoSeason() : v;
    }

    function apply() {
        var root = document.documentElement;
        root.setAttribute('data-season', current());

        // Tint the phone's browser bar too (only meta tags marked data-seasonal).
        var bg = getComputedStyle(root).getPropertyValue('--mint').trim();
        if (bg) {
            var metas = document.querySelectorAll('meta[name="theme-color"][data-seasonal]');
            for (var i = 0; i < metas.length; i++) metas[i].setAttribute('content', bg);
        }
    }

    function set(value) {
        try { localStorage.setItem(KEY, SEASONS.indexOf(value) !== -1 ? value : 'auto'); }
        catch (e) { /* storage unavailable: still apply for this page view */ }
        apply();
    }

    window.SproutTheme = { get: get, set: set, current: current, apply: apply, autoSeason: autoSeason, seasons: SEASONS };
    apply();

    /* ---------- settings popup (season picker) ---------- */

    function cap(t) { return t.charAt(0).toUpperCase() + t.slice(1); }

    function setupSettings() {
        var dialog = document.getElementById('settings-dialog');
        var button = document.getElementById('btn-settings');
        if (!dialog || !button) return;

        var radios = dialog.querySelectorAll('input[name="season"]');
        var caption = document.getElementById('season-caption');
        var closeBtn = document.getElementById('close-settings');

        button.innerHTML = GEAR;
        button.disabled = false;
        button.setAttribute('aria-controls', 'settings-dialog');
        button.setAttribute('aria-expanded', 'false');

        function refresh() {
            var pick = get();
            for (var i = 0; i < radios.length; i++) radios[i].checked = radios[i].value === pick;
            caption.textContent = pick === 'auto'
                ? 'Following today\u2019s date: ' + cap(current())
                : 'Showing ' + cap(pick) + '. Choose Auto to follow the date.';
        }

        function open() {
            refresh();
            dialog.hidden = false;
            requestAnimationFrame(function () { dialog.classList.add('open'); });
            button.setAttribute('aria-expanded', 'true');
            var checked = dialog.querySelector('input[name="season"]:checked');
            if (checked) checked.focus({ preventScroll: true });
        }

        function close() {
            dialog.classList.remove('open');
            dialog.hidden = true;
            button.setAttribute('aria-expanded', 'false');
            button.focus();
        }

        button.addEventListener('click', open);
        closeBtn.addEventListener('click', close);
        dialog.addEventListener('click', function (e) { if (e.target === dialog) close(); });
        document.addEventListener('keydown', function (e) { if (e.key === 'Escape' && !dialog.hidden) close(); });

        for (var i = 0; i < radios.length; i++) {
            radios[i].addEventListener('change', function (e) {
                set(e.target.value);   // applies instantly
                refresh();
            });
        }
    }

    document.addEventListener('DOMContentLoaded', setupSettings);
})();
