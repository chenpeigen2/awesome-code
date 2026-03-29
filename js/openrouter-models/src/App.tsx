import { useState, useMemo } from "react";
import { useModels } from "./hooks/useModels";
import { useHuggingFaceModels } from "./hooks/useHuggingFaceModels";
import { StatsBar } from "./components/StatsBar";
import { ProviderList } from "./components/ProviderList";
import { ModelTable } from "./components/ModelTable";
import { HFStatsBar } from "./components/HFStatsBar";
import { HFAuthorList } from "./components/HFAuthorList";
import { HFModelTable } from "./components/HFModelTable";
import { SearchBar } from "./components/SearchBar";
import "./App.css";

const HF_PAGE_SIZE = 100;

type Tab = "openrouter" | "huggingface";

function App() {
  const [tab, setTab] = useState<Tab>("openrouter");
  const [search, setSearch] = useState("");

  return (
    <div className="flex flex-col min-h-screen bg-zinc-950 text-zinc-200">
      <header className="flex items-center justify-between px-6 py-4 border-b border-zinc-700 bg-zinc-900 gap-4 flex-wrap">
        <div className="flex items-center gap-4">
          <h1 className="text-lg font-semibold flex items-center gap-2 whitespace-nowrap">
            <span className="text-indigo-500">&#9881;</span>
            Models Explorer
          </h1>
          <nav className="flex gap-1 bg-zinc-800 rounded-lg p-1">
            <button
              className={`px-3 py-1 text-sm rounded-md transition-colors cursor-pointer ${
                tab === "openrouter"
                  ? "bg-indigo-500 text-white"
                  : "text-zinc-400 hover:text-zinc-200"
              }`}
              onClick={() => { setTab("openrouter"); setSearch(""); }}
            >
              OpenRouter
            </button>
            <button
              className={`px-3 py-1 text-sm rounded-md transition-colors cursor-pointer ${
                tab === "huggingface"
                  ? "bg-yellow-500 text-zinc-900"
                  : "text-zinc-400 hover:text-zinc-200"
              }`}
              onClick={() => { setTab("huggingface"); setSearch(""); }}
            >
              HuggingFace
            </button>
          </nav>
        </div>
        <SearchBar value={search} onChange={setSearch} />
      </header>

      {tab === "openrouter" ? (
        <OpenRouterView search={search} />
      ) : (
        <HuggingFaceView search={search} />
      )}
    </div>
  );
}

function OpenRouterView({ search }: { search: string }) {
  const { models, providerGroups, loading, error } = useModels();
  const [selectedProvider, setSelectedProvider] = useState<string | null>(null);

  const filteredModels = useMemo(() => {
    let result = models;
    if (selectedProvider) {
      result = result.filter((m) => m.id.startsWith(selectedProvider + "/"));
    }
    if (search) {
      const q = search.toLowerCase();
      result = result.filter(
        (m) =>
          m.name.toLowerCase().includes(q) ||
          m.id.toLowerCase().includes(q) ||
          m.description.toLowerCase().includes(q)
      );
    }
    return result;
  }, [models, selectedProvider, search]);

  if (loading) {
    return (
      <LoadingSpinner label="Loading models from OpenRouter API..." color="border-t-indigo-500" />
    );
  }

  if (error) {
    return <ErrorDisplay error={error} />;
  }

  return (
    <>
      <StatsBar models={models} providers={providerGroups} />
      <div className="flex flex-1 overflow-hidden max-md:flex-col">
        <ProviderList
          providers={providerGroups}
          selected={selectedProvider}
          onSelect={setSelectedProvider}
        />
        <main className="flex-1 flex flex-col overflow-hidden">
          <ResultsInfo
            count={filteredModels.length}
            total={models.length}
            filtered={!!selectedProvider}
            onClear={() => setSelectedProvider(null)}
          />
          <ModelTable models={filteredModels} />
        </main>
      </div>
    </>
  );
}

function HuggingFaceView({ search }: { search: string }) {
  const {
    allModels, allAuthorGroups, loading, fetchProgress, error,
    page, goNext, goPrev, resetPage,
  } = useHuggingFaceModels();
  const [selectedAuthor, setSelectedAuthor] = useState<string | null>(null);

  // Filter from ALL models (not just current page)
  const filteredAll = useMemo(() => {
    let result = allModels;
    if (selectedAuthor) {
      result = result.filter((m) => m.id.startsWith(selectedAuthor + "/"));
    }
    if (search) {
      const q = search.toLowerCase();
      result = result.filter(
        (m) =>
          m.id.toLowerCase().includes(q) ||
          m.tags.some((t) => t.toLowerCase().includes(q)) ||
          (m.pipeline_tag && m.pipeline_tag.toLowerCase().includes(q)) ||
          (m.library_name && m.library_name.toLowerCase().includes(q))
      );
    }
    return result;
  }, [allModels, selectedAuthor, search]);

  // Paginate the filtered results
  const totalPages = Math.ceil(filteredAll.length / HF_PAGE_SIZE);
  const currentPage = Math.min(page, totalPages - 1);
  const pagedModels = filteredAll.slice(currentPage * HF_PAGE_SIZE, (currentPage + 1) * HF_PAGE_SIZE);
  const totalDownloads = pagedModels.reduce((sum, m) => sum + m.downloads, 0);

  // Author groups for sidebar — show all, with counts reflecting the full dataset
  // But highlight which ones have matches in current filtered view
  const filteredAuthorSet = new Set(filteredAll.map((m) => m.id.split("/")[0]));

  const handleAuthorSelect = (author: string | null) => {
    setSelectedAuthor(author);
    resetPage();
  };

  if (error && allModels.length === 0) {
    return <ErrorDisplay error={error} />;
  }

  return (
    <>
      <HFStatsBar
        allModels={allModels}
        filteredCount={filteredAll.length}
        totalDownloads={totalDownloads}
        page={currentPage}
        totalPages={totalPages}
      />
      {loading && (
        <div className="px-5 py-2 bg-zinc-900 border-b border-zinc-700 flex items-center gap-3">
          <div className="w-4 h-4 border-2 border-zinc-600 border-t-yellow-400 rounded-full animate-spin" />
          <span className="text-xs text-zinc-500">
            Fetching models: {fetchProgress.toLocaleString()} loaded...
          </span>
          <div className="flex-1 h-1.5 bg-zinc-800 rounded-full overflow-hidden">
            <div
              className="h-full bg-yellow-500 rounded-full transition-all duration-300"
              style={{ width: `${Math.min(100, (fetchProgress / 10000) * 100)}%` }}
            />
          </div>
        </div>
      )}
      <div className="flex flex-1 overflow-hidden max-md:flex-col">
        <HFAuthorList
          authors={allAuthorGroups}
          selected={selectedAuthor}
          onSelect={handleAuthorSelect}
          highlightSet={filteredAuthorSet}
        />
        <main className="flex-1 flex flex-col overflow-hidden">
          <ResultsInfo
            count={filteredAll.length}
            total={allModels.length}
            filtered={!!selectedAuthor || !!search}
            onClear={() => { setSelectedAuthor(null); resetPage(); }}
          />
          <HFModelTable models={pagedModels} />
          <Pagination
            page={currentPage}
            totalPages={totalPages}
            onPrev={goPrev}
            onNext={goNext}
            accent="yellow"
          />
        </main>
      </div>
    </>
  );
}

function Pagination({
  page, totalPages, onPrev, onNext, accent,
}: {
  page: number;
  totalPages: number;
  onPrev: () => void;
  onNext: () => void;
  accent: "indigo" | "yellow";
}) {
  const base = "px-4 py-2 text-sm rounded-lg disabled:opacity-30 disabled:cursor-not-allowed transition-colors cursor-pointer";
  const nextClass = accent === "yellow"
    ? `${base} bg-yellow-500/15 text-yellow-400 hover:bg-yellow-500 hover:text-zinc-900`
    : `${base} bg-indigo-500/15 text-indigo-400 hover:bg-indigo-500 hover:text-white`;

  return (
    <div className="flex items-center justify-between px-5 py-3 border-t border-zinc-700 bg-zinc-900">
      <button disabled={page <= 0} onClick={onPrev}
        className={`${base} bg-zinc-800 text-zinc-300 hover:bg-zinc-700`}>
        &larr; Prev
      </button>
      <span className="text-sm text-zinc-500">
        Page {page + 1} / {totalPages || 1}
      </span>
      <button disabled={page >= totalPages - 1} onClick={onNext} className={nextClass}>
        Next &rarr;
      </button>
    </div>
  );
}

function LoadingSpinner({ label, color }: { label: string; color: string }) {
  return (
    <div className="flex flex-col items-center justify-center min-h-[60vh] gap-4 text-zinc-500">
      <div className={`w-9 h-9 border-3 border-zinc-700 ${color} rounded-full animate-spin`} />
      <p>{label}</p>
    </div>
  );
}

function ErrorDisplay({ error }: { error: string }) {
  return (
    <div className="flex flex-col items-center justify-center min-h-[60vh] gap-4 text-zinc-400">
      <h2 className="text-xl font-semibold text-red-400">Error</h2>
      <p>{error}</p>
    </div>
  );
}

function ResultsInfo({
  count, total, filtered, onClear,
}: {
  count: number;
  total: number;
  filtered: boolean;
  onClear: () => void;
}) {
  return (
    <div className="flex items-center gap-3 px-5 py-3 text-xs text-zinc-500 border-b border-zinc-700">
      Showing {count.toLocaleString()} of {total.toLocaleString()} models
      {filtered && (
        <button
          className="bg-indigo-500/15 text-indigo-400 text-xs px-2.5 py-1 rounded hover:bg-indigo-500 hover:text-white transition-colors cursor-pointer"
          onClick={onClear}
        >
          Clear filter
        </button>
      )}
    </div>
  );
}

export default App;
