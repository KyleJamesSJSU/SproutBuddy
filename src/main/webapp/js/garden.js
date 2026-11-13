/* =====================================================================
   SproutBuddy – garden.js
   Dashboard: plant carousel, today's reminders, friends' streaks.
   ===================================================================== */
(function () {
    'use strict';
 
    const el = SB_UI.el, ICONS = SB_UI.ICONS;
    const state = SB.state;
    const $ = function (sel) { return document.querySelector(sel); };
 
    const TITLES = { garden: 'Your Garden', reminders: 'Reminders', friends: 'Friends' };
    const TASK_NAME = { sun: 'Sunshine', water: 'Water' };
    const AVATAR_COLORS = ['#00B98B', '#6C8EF5', '#F29B5C', '#B77BE8', '#E8708A'];
 
    SB_UI.initTopbar();
 
    /* =========================== GARDEN =========================== */
 
    const carousel = $('#carousel');
    const dots = $('#dots');
    const prev = $('#prev');
    const next = $('#next');
    prev.innerHTML = ICONS.left;
    next.innerHTML = ICONS.right;
 
    function statusFor(plant) {
        const info = SB.dayInfo(plant, SB.today());
        if (info.status === 'none') return { text: 'nothing due today', ok: true };
        if (info.status === 'done') return { text: 'all done today', ok: true };
        const left = info.due.filter(function (k) { return info.done.indexOf(k) === -1; })
                             .map(function (k) { return k === 'sun' ? 'sunshine' : 'water'; });
        return { text: left.join(' & ') + ' due today', ok: false };
    }
 
    function renderGarden() {
        carousel.replaceChildren();
        dots.replaceChildren();
 
        if (!state.plants.length) {
            carousel.append(el('p', { class: 'empty', text: 'No plants yet.' }));
            prev.hidden = next.hidden = true;
            return;
        }
 
        state.plants.forEach(function (plant) {
            const st = statusFor(plant);
            carousel.append(
                el('a', { class: 'slide', href: 'plant.html?id=' + plant.id },
                    SB_UI.photoNode(plant),
                    el('span', { class: 'plant-name', text: plant.nickname }),
                    el('span', { class: 'plant-status' + (st.ok ? ' ok' : ''), text: st.text })));
            dots.append(el('span', { class: 'dot' }));
        });
 
        let saved = 0;
        try { saved = Number(sessionStorage.getItem('sb-slide')) || 0; } catch (e) { /* ignore */ }
        carousel.scrollLeft = saved * carousel.clientWidth;
        syncCarousel();
    }
 
    function currentIndex() {
        return Math.round(carousel.scrollLeft / Math.max(1, carousel.clientWidth));
    }
 
    function syncCarousel() {
        const i = currentIndex();
        const last = state.plants.length - 1;
        prev.disabled = i <= 0;
        next.disabled = i >= last;
        Array.prototype.forEach.call(dots.children, function (d, n) { d.classList.toggle('on', n === i); });
        try { sessionStorage.setItem('sb-slide', String(i)); } catch (e) { /* ignore */ }
    }
 
    carousel.addEventListener('scroll', syncCarousel, { passive: true });
    prev.addEventListener('click', function () { carousel.scrollBy({ left: -carousel.clientWidth, behavior: 'smooth' }); });
    next.addEventListener('click', function () { carousel.scrollBy({ left:  carousel.clientWidth, behavior: 'smooth' }); });
 
    /* ========================= REMINDERS ========================== */
 
    const remindersPanel = $('#panel-reminders');
 
    function toggleReminder(r, value) {
        SB.setDone(r.plantId, r.task, SB.today(), value);
        renderReminders();
        renderGarden();
    }
 
    // Swipe a row to the right to mark it complete (mouse or touch).
    function attachSwipe(body, onComplete) {
        let startX = 0, dx = 0, active = false;
 
        body.addEventListener('pointerdown', function (e) {
            if (e.target.closest('.check-btn')) return;
            active = true;
            startX = e.clientX;
            dx = 0;
            body.setPointerCapture(e.pointerId);
            body.style.transition = 'none';
        });
 
        body.addEventListener('pointermove', function (e) {
            if (!active) return;
            dx = Math.max(0, e.clientX - startX);
            body.style.transform = 'translateX(' + dx + 'px)';
        });
 
        function end() {
            if (!active) return;
            active = false;
            body.style.transition = 'transform 0.2s';
            if (dx > 90) {
                body.style.transform = 'translateX(110%)';
                setTimeout(onComplete, 180);
            } else {
                body.style.transform = '';
            }
        }
 
        body.addEventListener('pointerup', end);
        body.addEventListener('pointercancel', end);
    }
 
    function reminderRow(r) {
        const thumb = el('div', { class: 'thumb' }, SB_UI.photoNode({ photo: r.photo, nickname: r.plantName }));
 
        const check = el('button', {
            class: 'check-btn', type: 'button',
            'aria-label': (r.done ? 'Mark not done: ' : 'Mark done: ') + TASK_NAME[r.task] + ' ' + r.plantName,
            html: ICONS.check,
            onclick: function () { toggleReminder(r, !r.done); }
        });
 
        const body = el('div', { class: 'reminder-body' },
            check,
            el('span', { class: 'task-icon', html: ICONS[r.task === 'sun' ? 'sun' : 'drop'] }),
            el('span', { class: 'reminder-text' },
                el('strong', { text: TASK_NAME[r.task] }),
                el('span', { text: r.plantName })),
            thumb);
 
        const row = el('li', { class: 'reminder' + (r.done ? ' done' : '') },
            el('span', { class: 'swipe-hint', html: ICONS.check }),
            body);
 
        if (!r.done) attachSwipe(body, function () { toggleReminder(r, true); });
        return row;
    }
 
    function upcomingRow(r) {
        const date = SB.parse(r.date).toLocaleDateString(undefined, {
            weekday: 'short',
            month: 'short',
            day: 'numeric'
        });
        return el('li', { class: 'upcoming-reminder' },
            el('div', { class: 'upcoming-date', text: date }),
            el('span', { class: 'task-icon', html: ICONS[r.task === 'sun' ? 'sun' : 'drop'] }),
            el('span', { class: 'reminder-text' },
                el('strong', { text: TASK_NAME[r.task] }),
                el('span', { text: r.plantName })));
    }

    function renderReminders() {
        const items = SB.remindersToday();
        const upcoming = SB.remindersUpcoming(7);
        const doneCount = items.filter(function (r) { return r.done; }).length;
        const pending = items.filter(function (r) { return !r.done; });
        const done = items.filter(function (r) { return r.done; });
        const dateText = new Date().toLocaleDateString(undefined, { weekday: 'long', month: 'short', day: 'numeric' });
 
        remindersPanel.replaceChildren(
            el('div', { class: 'panel-head' },
                el('h2', { text: 'Today' }),
                el('p', { text: dateText + (items.length ? ' · ' + doneCount + ' of ' + items.length + ' done' : '') })),
            items.length
                ? el('div', { class: 'progress', role: 'progressbar', 'aria-valuemin': '0', 'aria-valuemax': String(items.length), 'aria-valuenow': String(doneCount) },
                      el('span', { style: 'width:' + (doneCount / items.length * 100) + '%' }))
                : null);
 
        if (!items.length) {
            remindersPanel.append(el('p', { class: 'empty', text: 'Nothing due today. Enjoy the day!' }));
        } else {
            if (pending.length) {
                remindersPanel.append(el('ul', { class: 'reminder-list' }, pending.map(reminderRow)));
                remindersPanel.append(el('p', { class: 'swipe-tip', text: 'Swipe right or tap the circle to check off' }));
            } else {
                remindersPanel.append(el('p', { class: 'empty', text: 'All caught up for today!' }));
            }

            if (done.length) {
                remindersPanel.append(el('p', { class: 'list-label', text: 'Completed' }));
                remindersPanel.append(el('ul', { class: 'reminder-list' }, done.map(reminderRow)));
            }
        }

        if (upcoming.length) {
            remindersPanel.append(el('p', { class: 'list-label', text: 'Upcoming' }));
            remindersPanel.append(el('ul', { class: 'reminder-list' }, upcoming.map(upcomingRow)));
        }
    }
 
    /* ========================== FRIENDS =========================== */
 
    const friendsPanel = $('#panel-friends');
 
    function renderFriends() {
        const friends = state.friends.slice().sort(function (a, b) { return b.streak - a.streak; });
 
        friendsPanel.replaceChildren(
            el('div', { class: 'panel-head' },
                el('h2', { text: 'Friends\u2019 streaks' }),
                el('p', { text: 'Plant care over the last 7 days' })),
            el('div', { style: 'height:16px' }),
            el('ul', { class: 'friend-list' }, friends.map(function (f, i) {
                return el('li', { class: 'friend' },
                    el('div', { class: 'friend-avatar', style: 'background:' + AVATAR_COLORS[i % AVATAR_COLORS.length], text: f.name[0].toUpperCase() }),
                    el('div', { class: 'friend-main' },
                        el('div', { class: 'friend-name', text: f.name }),
                        el('div', { class: 'friend-last', text: f.last }),
                        el('div', { class: 'friend-week', 'aria-hidden': 'true' },
                            f.week.map(function (on) { return el('i', { class: on ? 'on' : '' }); }))),
                    el('span', { class: 'streak-badge', text: f.streak + (f.streak === 1 ? ' day' : ' days') }));
            })));
    }
 
    /* ============================ TABS ============================ */
 
    const tabs = Array.prototype.slice.call(document.querySelectorAll('.tabs button'));
    const panels = { garden: $('#panel-garden'), reminders: remindersPanel, friends: friendsPanel };
 
    function showTab(name) {
        if (!panels[name]) name = 'garden';
        tabs.forEach(function (t) { t.setAttribute('aria-selected', String(t.dataset.tab === name)); });
        Object.keys(panels).forEach(function (k) { panels[k].hidden = k !== name; });
        $('#page-title').textContent = TITLES[name];
        document.title = TITLES[name] + ' | SproutBuddy';
        history.replaceState(null, '', name === 'garden' ? location.pathname : '#' + name);
        if (name === 'garden') {
            const saved = currentIndex();
            carousel.scrollLeft = saved * carousel.clientWidth;
            syncCarousel();
        }
    }
 
    tabs.forEach(function (t) { t.addEventListener('click', function () { showTab(t.dataset.tab); }); });
 
    /* ============================ INIT ============================ */
 
    renderGarden();
    renderReminders();
    renderFriends();
    showTab(location.hash.slice(1));
    window.addEventListener('hashchange', function () { showTab(location.hash.slice(1)); });
})();
 