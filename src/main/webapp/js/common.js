/* =====================================================================
   SproutBuddy – common.js
   Shared UI helpers for the garden + plant pages:
   - el()          builds DOM nodes safely (no innerHTML with user text)
   - icons
   - photoNode()   plant photo (or a sprout placeholder)
   - openSheet()   bottom sheet used for account / settings / edit forms
   - initTopbar()  wires up the account + settings buttons
   ===================================================================== */
(function () {
    'use strict';
 
    const ICONS = {
        sun:  '<svg viewBox="0 0 24 24" aria-hidden="true"><circle cx="12" cy="12" r="4.5" fill="#F6C315"/><path d="M12 2.5v3M12 18.5v3M2.5 12h3M18.5 12h3M5.3 5.3l2.1 2.1M16.6 16.6l2.1 2.1M18.7 5.3l-2.1 2.1M7.4 16.6l-2.1 2.1" stroke="#F6C315" stroke-width="2" stroke-linecap="round"/></svg>',
        drop: '<svg viewBox="0 0 24 24" aria-hidden="true"><path d="M12 3S5.5 10.2 5.5 14.8a6.5 6.5 0 0 0 13 0C18.5 10.2 12 3 12 3z" fill="#4AA8F0"/><path d="M9 15a3.2 3.2 0 0 0 2.6 3.1" stroke="#fff" stroke-width="1.6" fill="none" stroke-linecap="round" opacity=".7"/></svg>',
        x:    '<svg viewBox="0 0 24 24" aria-hidden="true"><path d="M4 4l16 16M20 4L4 20" stroke="#F29B92" stroke-width="3" stroke-linecap="round"/></svg>',
        check:'<svg viewBox="0 0 24 24" aria-hidden="true"><path d="M5 12.5l4.5 4.5L19 7.5" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"/></svg>',
        gear: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 1 1-2.83 2.83l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-4 0v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 1 1-2.83-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1 0-4h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 1 1 2.83-2.83l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 4 0v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 1 1 2.83 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 0 4h-.09a1.65 1.65 0 0 0-1.51 1z"/></svg>',
        back: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M15 5l-7 7 7 7"/></svg>',
        pencil:'<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M17 3a2.83 2.83 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5L17 3z"/></svg>',
        camera:'<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M23 19a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h4l2-3h6l2 3h4a2 2 0 0 1 2 2z"/><circle cx="12" cy="13" r="4"/></svg>',
        left: '<svg viewBox="0 0 24 36" aria-hidden="true"><path d="M20 3L4 18l16 15z" fill="currentColor"/></svg>',
        right:'<svg viewBox="0 0 24 36" aria-hidden="true"><path d="M4 3l16 15L4 33z" fill="currentColor"/></svg>'
    };
 
    const SPROUT =
        '<svg viewBox="0 0 24 18" shape-rendering="crispEdges" aria-hidden="true">' +
        '<g fill="#B8E62A"><rect x="4" y="0" width="6" height="1"/><rect x="14" y="0" width="6" height="1"/></g>' +
        '<g fill="#8FD43A"><rect x="2" y="1" width="9" height="1"/><rect x="13" y="1" width="9" height="1"/></g>' +
        '<g fill="#5FB83A"><rect x="3" y="2" width="8" height="1"/><rect x="13" y="2" width="8" height="1"/></g>' +
        '<rect x="11" y="2" width="2" height="12" fill="#3E9E3A"/>' +
        '<g fill="#B8E62A"><rect x="7" y="13" width="10" height="1"/><rect x="5" y="14" width="14" height="1"/><rect x="4" y="15" width="16" height="1"/><rect x="5" y="16" width="14" height="1"/><rect x="7" y="17" width="10" height="1"/></g></svg>';
 
    /* ---------- tiny DOM builder ---------- */
 
    function el(tag, props) {
        const n = document.createElement(tag);
        for (const [k, v] of Object.entries(props || {})) {
            if (v == null || v === false) continue;
            if (k === 'class') n.className = v;
            else if (k === 'text') n.textContent = v;
            else if (k === 'html') n.innerHTML = v;            // trusted constants only
            else if (k.startsWith('on')) n.addEventListener(k.slice(2), v);
            else n.setAttribute(k, v === true ? '' : v);
        }
        for (const kid of Array.prototype.slice.call(arguments, 2).flat()) {
            if (kid == null || kid === false) continue;
            n.append(kid.nodeType ? kid : document.createTextNode(kid));
        }
        return n;
    }
 
    function photoNode(plant) {
        const box = el('div', { class: 'photo' });
        if (plant.photo) box.append(el('img', { src: plant.photo, alt: plant.nickname }));
        else box.append(el('div', { class: 'placeholder', html: SPROUT }));
        return box;
    }
 
    /* ---------- bottom sheet ---------- */
 
    function openSheet(title, body) {
        const host = document.querySelector('.app-screen');
        const previous = document.activeElement;
 
        const close = function () {
            backdrop.classList.remove('open');
            document.removeEventListener('keydown', onKey);
            setTimeout(function () { backdrop.remove(); }, 250);
            if (previous && previous.focus) previous.focus();
        };
        const onKey = function (e) { if (e.key === 'Escape') close(); };
 
        const backdrop = el('div', { class: 'sheet-backdrop', onclick: function (e) { if (e.target === backdrop) close(); } },
            el('div', { class: 'sheet', role: 'dialog', 'aria-modal': 'true', 'aria-label': title },
                el('div', { class: 'sheet-handle' }),
                el('h2', { class: 'sheet-title', text: title }),
                body));
 
        host.append(backdrop);
        document.addEventListener('keydown', onKey);
        requestAnimationFrame(function () {
            backdrop.classList.add('open');
            const first = backdrop.querySelector('input, button');
            if (first) first.focus({ preventScroll: true });
        });
        return close;
    }
 
    function field(label, input) {
        return el('div', { class: 'field' }, el('label', { for: input.id, text: label }), input);
    }
 
    /* ---------- account + settings sheets ---------- */
 
    function openAccount() {
        const user = SB.state.user;
        const name = el('input', { type: 'text', id: 'acc-name', value: user.name, maxlength: '50', required: true, autocomplete: 'username' });
        const email = el('input', { type: 'email', id: 'acc-email', value: user.email, maxlength: '100', required: true, autocomplete: 'email' });
        let close;
 
        const form = el('form', {
            class: 'sheet-form',
            onsubmit: function (e) {
                e.preventDefault();
                user.name = name.value.trim();
                user.email = email.value.trim();
                SB.save();           // TODO backend: POST /account
                refreshTopbar();
                close();
            }
        },
            el('div', { class: 'account-top' },
                el('div', { class: 'avatar-lg', text: (user.name[0] || '?').toUpperCase() }),
                el('p', { class: 'muted', text: 'Your SproutBuddy account' })),
            field('Name', name),
            field('Email', email),
            el('button', { type: 'submit', class: 'btn btn-block', text: 'Save changes' }),
            el('button', {
                type: 'button', class: 'btn btn-block btn-outline', text: 'Log out',
                onclick: function () { location.href = 'login.html'; }   // TODO backend: POST /logout first
            }));
 
        close = openSheet('Account', form);
    }
 
    function openSettings() {
        const s = SB.state.settings;
        const notif = el('input', { type: 'checkbox', id: 'set-notif', checked: s.notifications });
        const share = el('input', { type: 'checkbox', id: 'set-share', checked: s.shareStreaks });
        const time = el('input', { type: 'time', id: 'set-time', value: s.reminderTime });
        notif.checked = s.notifications;
        share.checked = s.shareStreaks;
        let close;
 
        const toggle = function (id, label, hint, input) {
            return el('div', { class: 'setting-row' },
                el('label', { for: id }, el('strong', { text: label }), el('span', { class: 'muted', text: hint })),
                el('span', { class: 'switch' }, input, el('span', { class: 'track' })));
        };
 
        const form = el('form', {
            class: 'sheet-form',
            onsubmit: function (e) {
                e.preventDefault();
                s.notifications = notif.checked;
                s.shareStreaks = share.checked;
                s.reminderTime = time.value || '09:00';
                SB.save();           // TODO backend: POST /settings
                close();
            }
        },
            toggle('set-notif', 'Daily reminders', 'Get a nudge for plants that need care', notif),
            el('div', { class: 'setting-row' },
                el('label', { for: 'set-time' }, el('strong', { text: 'Reminder time' }), el('span', { class: 'muted', text: 'When your daily nudge arrives' })),
                time),
            toggle('set-share', 'Share my streaks', 'Let friends see your plant care activity', share),
            el('button', { type: 'submit', class: 'btn btn-block', text: 'Save settings' }));
 
        close = openSheet('Settings', form);
    }
 
    /* ---------- top bar ---------- */
 
    function refreshTopbar() {
        const a = document.getElementById('btn-account');
        if (a) a.textContent = (SB.state.user.name[0] || '?').toUpperCase();
    }
 
    function initTopbar() {
        const gear = document.getElementById('btn-settings');
        const acct = document.getElementById('btn-account');
        if (gear) { gear.innerHTML = ICONS.gear; gear.addEventListener('click', openSettings); }
        if (acct) acct.addEventListener('click', openAccount);
        refreshTopbar();
    }
 
    window.SB_UI = { el, ICONS, SPROUT, photoNode, openSheet, field, initTopbar };
})();
 