/* =====================================================================
   SproutBuddy – store.js
   ---------------------------------------------------------------------
   FRONT-END STAND-IN FOR THE BACKEND.
 
   Right now there are no servlets for plants, reminders or friends, so
   this file keeps sample data in the browser (localStorage) so the
   dashboard and plant pages are fully clickable.
 
   When the backend exists, swap the functions below (getPlant,
   updatePlant, setDone, ...) for fetch() calls to your servlets.
   The pages only talk to this file, so nothing else should need to change.
 
   Handy while testing: type  SB.reset()  in the browser console to go
   back to the original sample data.
   ===================================================================== */
(function () {
    'use strict';
 
    const KEY = 'sproutbuddy-demo-v1';
    const DAY = 86400000;
    const TASKS = ['sun', 'water'];
    const FREQ_KEY = { sun: 'sunEveryDays', water: 'waterEveryDays' };
    const ANCHOR_KEY = { sun: 'sunAnchor', water: 'waterAnchor' };
 
    /* ---------- date helpers (all dates are 'YYYY-MM-DD' strings) ---------- */
 
    const pad = n => String(n).padStart(2, '0');
    const fmt = d => d.getFullYear() + '-' + pad(d.getMonth() + 1) + '-' + pad(d.getDate());
    const parse = s => { const [y, m, d] = s.split('-').map(Number); return new Date(y, m - 1, d); };
    const today = () => fmt(new Date());
 
    function daysBetween(a, b) {
        const A = parse(a), B = parse(b);
        return Math.round((Date.UTC(B.getFullYear(), B.getMonth(), B.getDate()) -
                           Date.UTC(A.getFullYear(), A.getMonth(), A.getDate())) / DAY);
    }
 
    function addDays(s, n) {
        const d = parse(s);
        d.setDate(d.getDate() + n);
        return fmt(d);
    }
 
    /* ---------- sample data ---------- */
 
    function seed() {
        const t = today();
 
        const plants = [
            { id: 1, nickname: 'Money Plant', species: 'Pilea peperomioides', photo: 'images/money-plant.jpg',
              sunEveryDays: 1, waterEveryDays: 3,  dateAdded: addDays(t, -39) },
            { id: 2, nickname: 'Pothos', species: 'Epipremnum aureum', photo: null,
              sunEveryDays: 2, waterEveryDays: 7,  dateAdded: addDays(t, -35) },
            { id: 3, nickname: 'Snake Plant', species: 'Dracaena trifasciata', photo: null,
              sunEveryDays: 3, waterEveryDays: 14, dateAdded: addDays(t, -30) },
            { id: 4, nickname: 'Monstera', species: 'Monstera deliciosa', photo: null,
              sunEveryDays: 1, waterEveryDays: 7,  dateAdded: addDays(t, -12) }
        ];
 
        // Fake care history: everything done on schedule except a few misses.
        plants.forEach(p => {
            p.sunAnchor = p.waterAnchor = p.dateAdded;
            p.log = {};
            const span = daysBetween(p.dateAdded, addDays(t, -1));
            for (let i = 0; i <= span; i++) {
                const ds = addDays(p.dateAdded, i);
                TASKS.forEach(task => {
                    if (dueBySchedule(p, task, ds) && (i * 5 + p.id * 3) % 13 !== 0) {
                        (p.log[ds] = p.log[ds] || {})[task] = true;
                    }
                });
            }
        });
 
        return {
            user: { name: 'Matilda', email: 'matilda@example.com' },
            settings: { notifications: true, reminderTime: '09:00', shareStreaks: true },
            plants: plants,
            friends: [
                { id: 1, name: 'Priya',  streak: 21, last: 'Watered Fern · 2h ago',            week: [1, 1, 1, 1, 1, 1, 1] },
                { id: 2, name: 'Jordan', streak: 9,  last: 'Gave Cactus sunshine · yesterday', week: [0, 1, 1, 1, 1, 1, 1] },
                { id: 3, name: 'Sam',    streak: 4,  last: 'Watered Basil · 3h ago',           week: [1, 0, 0, 1, 1, 1, 1] },
                { id: 4, name: 'Alex',   streak: 0,  last: 'Missed watering Ficus · 2d ago',   week: [1, 1, 1, 1, 0, 0, 0] }
            ]
        };
    }
 
    /* ---------- persistence ---------- */
 
    function load() {
        try {
            const raw = localStorage.getItem(KEY);
            if (raw) {
                const s = JSON.parse(raw);
                if (s && s.user && s.plants) return s;
            }
        } catch (e) { /* fall through to seed */ }
        const s = seed();
        try { localStorage.setItem(KEY, JSON.stringify(s)); } catch (e) { /* ignore */ }
        return s;
    }
 
    function save() {
        try {
            localStorage.setItem(KEY, JSON.stringify(SB.state));
            return true;
        } catch (e) {
            return false;   // e.g. storage full after a big photo
        }
    }
 
    function reset() {
        try { localStorage.removeItem(KEY); } catch (e) { /* ignore */ }
        location.reload();
    }
 
    /* ---------- plant logic ---------- */
 
    function getPlant(id) {
        return SB.state.plants.find(p => p.id === Number(id)) || null;
    }
 
    function updatePlant(id, patch) {
        const p = getPlant(id);
        if (!p) return false;
        Object.assign(p, patch);
        return save();
    }
 
    function freqLabel(n) {
        return n === 1 ? 'everyday' : 'every ' + n + ' days';
    }
 
    function updateFrequency(id, task, n) {
        const p = getPlant(id);
        if (!p || p[FREQ_KEY[task]] === n) return true;
        p[FREQ_KEY[task]] = n;
        p[ANCHOR_KEY[task]] = today();   // new schedule starts today
        return save();
    }
 
    // Is this task scheduled on this date? (ignores whether it was done)
    function dueBySchedule(p, task, ds) {
        const anchor = p[ANCHOR_KEY[task]];
        if (ds < anchor) return false;
        return daysBetween(anchor, ds) % p[FREQ_KEY[task]] === 0;
    }
 
    /*  What happened on a given day for a plant?
        status: 'none'    nothing scheduled
                'done'    everything scheduled was completed
                'missed'  something scheduled in the past was not completed
                'pending' today, still has tasks left
                'future'  hasn't happened yet                                */
    function dayInfo(p, ds) {
        const t = today();
        const done = TASKS.filter(k => p.log[ds] && p.log[ds][k]);
        const due = TASKS.filter(k => dueBySchedule(p, k, ds) || done.includes(k));
        let status;
        if (ds > t) status = 'future';
        else if (due.length === 0) status = 'none';
        else if (done.length === due.length) status = 'done';
        else status = ds === t ? 'pending' : 'missed';
        return { due: due, done: done, status: status };
    }
 
    function setDone(plantId, task, ds, val) {
        const p = getPlant(plantId);
        if (!p) return;
        const day = p.log[ds] || (p.log[ds] = {});
        if (val) day[task] = true; else delete day[task];
        if (!Object.keys(day).length) delete p.log[ds];
        save();
    }
 
    // Consecutive scheduled days completed, counting back from today.
    function streak(p) {
        let n = 0;
        let ds = today();
        while (ds >= p.dateAdded) {
            const s = dayInfo(p, ds).status;
            if (s === 'done') n++;
            else if (s === 'missed') break;
            ds = addDays(ds, -1);
        }
        return n;
    }
 
    // Everything due today across all plants (done ones included).
    function remindersToday() {
        const ds = today();
        const out = [];
        SB.state.plants.forEach(p => {
            TASKS.forEach(task => {
                const done = !!(p.log[ds] && p.log[ds][task]);
                if (done || dueBySchedule(p, task, ds)) {
                    out.push({ plantId: p.id, plantName: p.nickname, photo: p.photo, task: task, done: done });
                }
            });
        });
        return out.sort((a, b) => a.done - b.done);
    }
 
    /* ---------- export ---------- */
 
    const SB = {
        state: load(),
        save, reset,
        today, fmt, parse, addDays, daysBetween,
        getPlant, updatePlant, updateFrequency, freqLabel,
        dayInfo, setDone, streak, remindersToday,
        TASKS
    };
 
    window.SB = SB;
})();
 