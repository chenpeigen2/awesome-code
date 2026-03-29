export interface HFModel {
  _id: string;
  id: string;
  likes: number;
  private: boolean;
  downloads: number;
  tags: string[];
  pipeline_tag: string | null;
  library_name: string | null;
  createdAt: string;
  modelId: string;
}

export interface HFAuthorGroup {
  author: string;
  models: HFModel[];
  count: number;
}
