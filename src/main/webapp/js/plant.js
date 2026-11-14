/* =====================================================================
   SproutBuddy – plant.js
   Single plant page: name + photo, streak calendar, editable details.
   URL: plant.html?id=<plant id>
   ===================================================================== */
(function () {
    'use strict';
 
    const el = SB_UI.el, ICONS = SB_UI.ICONS;
    const $ = function (sel) { return document.querySelector(sel); };
 
    const id = Number(new URLSearchParams(location.search).get('id'));
    const plant = SB.getPlant(id);
    if (!plant) { location.replace('garden.html#garden'); return; }
 
    SB_UI.initTopbar();
    $('#btn-back').innerHTML = ICONS.back;
    $('#edit-name').innerHTML = ICONS.pencil;
    $('#camera-icon').innerHTML = ICONS.camera;
    $('#cal-prev').innerHTML = ICONS.back;
    $('#cal-next').innerHTML = ICONS.back.replace('M15 5l-7 7 7 7', 'M9 5l7 7-7 7');
    $('#legend').replaceChildren(
        el('span', {}, el('span', { html: ICONS.sun }), el('span', { html: ICONS.drop }), ' completed'),
        el('span', {}, el('span', { html: ICONS.x }), ' missed'));
 
    const now = new Date();
    const view = { y: now.getFullYear(), m: now.getMonth() };
 
    /* ---------- header: name + photo ---------- */
 
    function renderHead() {
        $('#plant-name').textContent = plant.nickname;
        document.title = plant.nickname + ' | SproutBuddy';
        $('#photo-slot').replaceChildren(SB_UI.photoNode(plant));
    }
 
    // Shrink the picked photo so it fits comfortably in browser storage.
    function resizeImage(file, max) {
        return new Promise(function (resolve, reject) {
            const url = URL.createObjectURL(file);
            const img = new Image();
            img.onload = function () {
                const scale = Math.min(1, max / Math.max(img.width, img.height));
                const c = document.createElement('canvas');
                c.width = Math.round(img.width * scale);
                c.height = Math.round(img.height * scale);
                c.getContext('2d').drawImage(img, 0, 0, c.width, c.height);
                URL.revokeObjectURL(url);
                resolve(c.toDataURL('image/jpeg', 0.85));
            };
            img.onerror = function () { URL.revokeObjectURL(url); reject(new Error('bad image')); };
            img.src = url;
        });
    }
 
    $('#photo-input').addEventListener('change', function (e) {
        const file = e.target.files[0];
        e.target.value = '';
        if (!file) return;
        resizeImage(file, 480).then(function (dataUrl) {
            // TODO backend: upload the file (multipart) and store its path in user_plants.photo
            if (!SB.updatePlant(id, { photo: dataUrl })) alert('That photo is too large to save. Try a smaller one.');
            renderHead();
        }).catch(function () { alert('Sorry, that image could not be loaded.'); });
    });
 
    $('#edit-name').addEventListener('click', function () {
        const input = el('input', { type: 'text', id: 'name-input', value: plant.nickname, maxlength: '100', required: true });
        let close;
        const form = el('form', {
            class: 'sheet-form',
            onsubmit: function (e) {
                e.preventDefault();
                SB.updatePlant(id, { nickname: input.value.trim() });   // TODO backend: user_plants.nickname
                renderHead();
                close();
            }
        },
            SB_UI.field('Plant name', input),
            el('button', { type: 'submit', class: 'btn btn-block', text: 'Save name' }));
        close = SB_UI.openSheet('Rename plant', form);
        input.select();
    });
 
    /* ---------- streak calendar ---------- */
 
    const MONTHS = ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August',
                    'September', 'October', 'November', 'December'];
 
    function cellMarks(info) {
        const marks = el('div', { class: 'marks' });
        const icon = function (k) { return ICONS[k === 'sun' ? 'sun' : 'drop']; };
 
        if (info.status === 'done') {
            info.done.forEach(function (k) { marks.append(el('span', { html: icon(k), style: 'display:contents' })); });
        } else if (info.status === 'missed') {
            marks.append(el('span', { class: 'big', html: ICONS.x, style: 'display:contents' }));
            marks.classList.add('big');
        } else if (info.status === 'pending') {
            info.due.forEach(function (k) {
                const done = info.done.indexOf(k) !== -1;
                marks.append(el('span', { class: done ? '' : 'ghost', html: icon(k), style: done ? 'display:contents' : '' }));
            });
        }
        return marks;
    }
 
    function renderCalendar() {
        const cal = $('#calendar');
        const today = SB.today();
        cal.replaceChildren();
        $('#cal-title').textContent = MONTHS[view.m] + ' ' + view.y;
 
        ['S', 'M', 'T', 'W', 'T', 'F', 'S'].forEach(function (d) { cal.append(el('div', { class: 'dow', text: d })); });
 
        const first = new Date(view.y, view.m, 1);
        const last = new Date(view.y, view.m + 1, 0);
        const start = new Date(first); start.setDate(1 - first.getDay());
        const end = new Date(last);    end.setDate(last.getDate() + (6 - last.getDay()));
 
        for (const d = new Date(start); d <= end; d.setDate(d.getDate() + 1)) {
            const ds = SB.fmt(d);
            const out = d.getMonth() !== view.m;
            const cell = el('div', { class: 'cell' + (out ? ' out' : '') + (ds === today ? ' today' : '') },
                el('span', { class: 'num', text: String(d.getDate()) }));
 
            if (!out) {
                const info = SB.dayInfo(plant, ds);
                const label = { done: 'completed', missed: 'missed', pending: 'still to do', none: 'nothing scheduled', future: 'upcoming' }[info.status];
                cell.setAttribute('role', 'img');
                cell.setAttribute('aria-label', MONTHS[d.getMonth()] + ' ' + d.getDate() + ': ' + label);
                cell.append(cellMarks(info));
            }
            cal.append(cell);
        }
 
        const n = SB.streak(plant);
        $('#streak-count').textContent = n ? n + ' day streak' : 'no streak yet';
    }
 
    $('#cal-prev').addEventListener('click', function () { view.m--; if (view.m < 0) { view.m = 11; view.y--; } renderCalendar(); });
    $('#cal-next').addEventListener('click', function () { view.m++; if (view.m > 11) { view.m = 0; view.y++; } renderCalendar(); });
 
    /* ---------- details (sunshine + water) ---------- */
 
    function renderDetails() {
        $('#edit-sun').replaceChildren(SB.freqLabel(plant.sunEveryDays), el('span', { html: ICONS.pencil }));
        $('#edit-water').replaceChildren(SB.freqLabel(plant.waterEveryDays), el('span', { html: ICONS.pencil }));
    }
 
    function editFrequency(task) {
        const current = task === 'sun' ? plant.sunEveryDays : plant.waterEveryDays;
        let n = current;
        let close;
 
        const out = el('output', { class: 'stepper-value' });
        const caption = el('p', { class: 'stepper-label' });
        const update = function () { out.textContent = String(n); caption.textContent = SB.freqLabel(n); };
 
        const stepper = el('div', { class: 'stepper' },
            el('button', { type: 'button', class: 'step-btn', 'aria-label': 'Fewer days', text: '\u2212',
                           onclick: function () { n = Math.max(1, n - 1); update(); } }),
            out,
            el('button', { type: 'button', class: 'step-btn', 'aria-label': 'More days', text: '+',
                           onclick: function () { n = Math.min(60, n + 1); update(); } }));
 
        const form = el('form', {
            class: 'sheet-form',
            onsubmit: function (e) {
                e.preventDefault();
                SB.updateFrequency(id, task, n);   // TODO backend: save care frequency for this plant
                renderDetails();
                renderCalendar();
                close();
            }
        },
            el('p', { class: 'muted', style: 'text-align:center;margin:0', text: 'Every how many days? (1 = everyday)' }),
            stepper,
            caption,
            el('button', { type: 'submit', class: 'btn btn-block', text: 'Save' }));
 
        update();
        close = SB_UI.openSheet(task === 'sun' ? 'Sunshine schedule' : 'Watering schedule', form);
    }
 
    $('#edit-sun').addEventListener('click', function () { editFrequency('sun'); });
    $('#edit-water').addEventListener('click', function () { editFrequency('water'); });
 
    /* ---------- init ---------- */
 
    renderHead();
    renderCalendar();
    renderDetails();
})();
 