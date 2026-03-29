import { useState, useEffect, useCallback, useRef } from "react";
import type { HFModel, HFAuthorGroup } from "../types/huggingface";

const PAGE_SIZE = 100;
const FETCH_BATCH = 100;
const MAX_MODELS = 10000;
const STORAGE_KEY = "hf_models_cache_v2";
const CACHE_TTL = 1000 * 60 * 60 * 24; // 24 hours

interface CacheData {
  models: HFModel[];
  timestamp: number;
}

function extractNextCursor(headers: Headers): string | null {
  const link = headers.get("Link");
  if (!link) return null;
  const match = link.match(/<[^?]*\?[^>]*cursor=([^&>]+)/);
  return match ? decodeURIComponent(match[1]) : null;
}

function buildUrl(cursor: string | null): string {
  const base = "https://huggingface.co/api/models";
  const params = new URLSearchParams({
    limit: String(FETCH_BATCH),
    sort: "downloads",
    direction: "-1",
  });
  if (cursor) params.set("cursor", cursor);
  return `${base}?${params.toString()}`;
}

/** Compute author groups from ALL models */
function computeAuthorGroups(models: HFModel[]): HFAuthorGroup[] {
  const map = new Map<string, HFModel[]>();
  for (const model of models) {
    const author = model.id.includes("/") ? model.id.split("/")[0] : "other";
    if (!map.has(author)) map.set(author, []);
    map.get(author)!.push(model);
  }
  return Array.from(map.entries())
    .map(([author, models]) => ({
      author,
      models,
      count: models.length,
    }))
    .sort((a, b) => b.count - a.count);
}

export function useHuggingFaceModels() {
  const [allModels, setAllModels] = useState<HFModel[]>([]);
  const [loading, setLoading] = useState(true);
  const [fetchProgress, setFetchProgress] = useState(0);
  const [error, setError] = useState<string | null>(null);
  const [page, setPage] = useState(0);
  const abortRef = useRef(false);

  useEffect(() => {
    abortRef.current = false;

    async function loadModels() {
      // Try cache first
      try {
        const cached = localStorage.getItem(STORAGE_KEY);
        if (cached) {
          const data: CacheData = JSON.parse(cached);
          if (Date.now() - data.timestamp < CACHE_TTL && data.models.length >= 100) {
            // Filter to last year even from cache
            const oneYearAgo = Date.now() - 365 * 24 * 60 * 60 * 1000;
            const recent = data.models.filter((m) => new Date(m.createdAt).getTime() >= oneYearAgo);
            setAllModels(recent);
            setLoading(false);
            setFetchProgress(data.models.length);
            return;
          }
        }
      } catch {
        // ignore cache errors
      }

      // Fetch all pages
      setLoading(true);
      const all: HFModel[] = [];
      let cursor: string | null = null;

      while (all.length < MAX_MODELS) {
        if (abortRef.current) return;
        try {
          const res = await fetch(buildUrl(cursor));
          if (!res.ok) throw new Error(`HTTP ${res.status}`);
          const data = (await res.json()) as HFModel[];
          if (data.length === 0) break;
          all.push(...data);
          setFetchProgress(all.length);
          setAllModels([...all]);
          cursor = extractNextCursor(res.headers);
          if (!cursor) break;
        } catch (err) {
          if (all.length > 0) break;
          setError(err instanceof Error ? err.message : "Unknown error");
          setLoading(false);
          return;
        }
      }

      // Sort newest first
      all.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime());

      // Keep only models from the last year
      const oneYearAgo = Date.now() - 365 * 24 * 60 * 60 * 1000;
      const recent = all.filter((m) => new Date(m.createdAt).getTime() >= oneYearAgo);

      setAllModels(recent);
      setLoading(false);

      try {
        const cache: CacheData = { models: recent, timestamp: Date.now() };
        localStorage.setItem(STORAGE_KEY, JSON.stringify(cache));
      } catch {
        // ignore
      }
    }

    loadModels();
    return () => { abortRef.current = true; };
  }, []);

  // Author groups from ALL models (not just current page)
  const allAuthorGroups = computeAuthorGroups(allModels);

  const goNext = useCallback(() => setPage((p) => p + 1), []);
  const goPrev = useCallback(() => setPage((p) => Math.max(p - 1, 0)), []);
  const resetPage = useCallback(() => setPage(0), []);

  return {
    allModels,
    allAuthorGroups,
    loading,
    fetchProgress,
    error,
    page,
    PAGE_SIZE,
    goNext,
    goPrev,
    resetPage,
  };
}
