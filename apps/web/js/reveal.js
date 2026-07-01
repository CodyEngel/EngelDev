(function () {
  var els = document.querySelectorAll('[data-reveal]');
  if (!els.length) return;

  var reduce = window.matchMedia('(prefers-reduced-motion: reduce)').matches;

  // If motion is off or IntersectionObserver is unavailable, reveal everything now.
  if (reduce || !('IntersectionObserver' in window)) {
    els.forEach(function (el) { el.classList.add('is-visible'); });
    return;
  }

  var observer = new IntersectionObserver(function (entries) {
    entries.forEach(function (entry) {
      if (entry.isIntersecting) {
        entry.target.classList.add('is-visible');
        observer.unobserve(entry.target);
      }
    });
  }, { rootMargin: '0px 0px -40px 0px', threshold: 0 });

  els.forEach(function (el) { observer.observe(el); });
})();
