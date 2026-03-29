import type { HFModel } from "../types/huggingface";
import { formatNumber } from "./HFStatsBar";

interface HFModelTableProps {
  models: HFModel[];
}

function getLicense(tags: string[]): string {
  const license = tags.find((t) => t.startsWith("license:"));
  return license ? license.replace("license:", "") : "-";
}

function getLanguages(tags: string[]): string[] {
  return tags.filter((t) => t.length === 2 && !t.includes("-") && t === t.toLowerCase());
}

export function HFModelTable({ models }: HFModelTableProps) {
  if (models.length === 0) {
    return <div className="p-10 text-center text-zinc-500">No models found</div>;
  }

  return (
    <div className="flex-1 overflow-auto">
      <table className="w-full border-collapse text-sm">
        <thead>
          <tr className="sticky top-0 z-10">
            {["Model", "Pipeline", "Library", "License", "Lang", "Downloads", "Likes", "Created"].map(
              (h) => (
                <th
                  key={h}
                  className="bg-zinc-900 px-4 py-2.5 text-left text-xs uppercase tracking-wide text-zinc-500 border-b border-zinc-700 whitespace-nowrap"
                >
                  {h}
                </th>
              )
            )}
          </tr>
        </thead>
        <tbody>
          {models.map((model) => (
            <tr key={model.id} className="hover:bg-zinc-800/60 transition-colors">
              <td className="px-4 py-2.5 border-b border-zinc-800 min-w-60">
                <a
                  href={`https://huggingface.co/${model.id}`}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="font-medium text-zinc-100 hover:text-yellow-400 transition-colors"
                >
                  {model.id}
                </a>
              </td>
              <td className="px-4 py-2.5 border-b border-zinc-800">
                {model.pipeline_tag ? (
                  <span className="text-xs px-2 py-0.5 rounded bg-yellow-500/15 text-yellow-400 whitespace-nowrap">
                    {model.pipeline_tag}
                  </span>
                ) : (
                  <span className="text-xs text-zinc-600">-</span>
                )}
              </td>
              <td className="px-4 py-2.5 border-b border-zinc-800 text-zinc-400">
                {model.library_name || "-"}
              </td>
              <td className="px-4 py-2.5 border-b border-zinc-800 text-zinc-400">
                {getLicense(model.tags)}
              </td>
              <td className="px-4 py-2.5 border-b border-zinc-800 text-zinc-400">
                {getLanguages(model.tags).join(", ") || "-"}
              </td>
              <td className="px-4 py-2.5 border-b border-zinc-800 font-mono text-xs text-emerald-400">
                {formatNumber(model.downloads)}
              </td>
              <td className="px-4 py-2.5 border-b border-zinc-800 text-zinc-300">
                {model.likes > 0 ? `❤ ${model.likes}` : "-"}
              </td>
              <td className="px-4 py-2.5 border-b border-zinc-800 text-xs text-zinc-500 whitespace-nowrap">
                {new Date(model.createdAt).toLocaleDateString("zh-CN")}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
